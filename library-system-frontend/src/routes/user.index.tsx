import { createFileRoute, Link } from "@tanstack/react-router";
import { BookOpen, BookPlus, ClipboardList, Heart, MessageSquare, Loader2, X } from "lucide-react";
import { useMemo, useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { PageHeader } from "@/components/layout/PageHeader";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { useAsync } from "@/hooks/useAsync";
import { useAuth } from "@/context/AuthContext";
import { getMyBorrowRecords } from "@/services/borrowService";
import { getAllBooks, getPopularBooks, getReservationNotifications, getMyReservations } from "@/services/bookService";
import { toast } from "sonner";
import { formatDate, daysUntil, isDueSoon, dueSoonText } from "@/lib/format";
import { bookActionClass, getBookAction } from "@/lib/bookAction";
import { getActiveBorrowedBookIds, isActiveBorrowRecord } from "@/lib/borrowRecord";
import { cancelReservation, fulfillReservation } from "@/services/reservationService";
import type { Book } from "@/types/book";
import { BookDetailDialog } from "@/components/books/BookDetailDialog";

export const Route = createFileRoute("/user/")({ component: UserDashboardPage });
type RankMode = "BORROW" | "RATING";

function UserDashboardPage() {
  const { user } = useAuth();
  const [rankMode, setRankMode] = useState<RankMode>("BORROW");
  const [detail, setDetail] = useState<Book | null>(null);
  const [borrowDays, setBorrowDays] = useState<1 | 3 | 7 | 14>(7);
  const [reservationActionId, setReservationActionId] = useState<number | null>(null);

  const { data, loading, error, refetch } = useAsync(() => getMyBorrowRecords(user!.studentId).then((r) => r.data), [user?.studentId]);
  const { data: rankedBooks, refetch: refetchRanked, setData: setRankedBooks } = useAsync(() => getPopularBooks(rankMode === "BORROW" ? "borrow" : "rating", 20).then((r) => r.data), [rankMode]);
  const { data: reservationNotifications, refetch: refetchReservationNotifications } = useAsync(() => user ? getReservationNotifications(user.userId).then((r) => r.data) : Promise.resolve([]), [user?.userId]);
  const { data: myReservations, refetch: refetchMyReservations } = useAsync(() => user ? getMyReservations(user.userId).then((r) => r.data) : Promise.resolve([]), [user?.userId]);

  const active = (data ?? []).filter(isActiveBorrowRecord);
  const overdue = (data ?? []).filter((r) => r.status === "OVERDUE");
  const dueSoon = active.filter((r) => r.status !== "OVERDUE" && isDueSoon(r.dueDate));
  const activeBorrowedBookIds = getActiveBorrowedBookIds(data ?? [], rankedBooks ?? []);
  const notifiedReservations = (myReservations ?? []).filter((r) =>
    r.status === "NOTIFIED"
      && r.canBorrowNotified
      && (!r.expiresAt || new Date(r.expiresAt).getTime() >= Date.now()),
  );
  const waitingReservations = (myReservations ?? [])
    .filter((r) => r.status === "WAITING")
    .filter((r) => !notifiedReservations.some((n) => n.title === r.title));

  const popularBooks = useMemo(() => (rankedBooks ?? []).slice(0, 5), [rankedBooks]);
  const reservationByBookId = new Map((myReservations ?? []).map((r) => [r.bookId, r]));

  const showPopularDetail = async (bookId: number, title: string, status: "AVAILABLE" | "BORROWED") => {
    setBorrowDays(user?.level === "VIP" ? 14 : 7);
    try {
      const books = await getAllBooks();
      const latestBook = books.data.find((book) => Number(book.id) === bookId);
      if (latestBook) {
        setRankedBooks((rankedBooks ?? []).map((book) =>
          book.bookId === bookId ? { ...book, status: latestBook.status === "AVAILABLE" ? "AVAILABLE" : "BORROWED" } : book,
        ));
      }
      setDetail(latestBook ?? {
        id: String(bookId), title, authors: "", subjects: "", isbns: [], publisher: "", publishYear: 0, edition: "", format: "", source: "", note: "", status,
      } as Book);
    } catch {
      setDetail({ id: String(bookId), title, authors: "", subjects: "", isbns: [], publisher: "", publishYear: 0, edition: "", format: "", source: "", note: "", status } as Book);
    }
  };

  const refreshDashboard = () => Promise.all([
    refetch(),
    refetchRanked(),
    refetchReservationNotifications(),
    refetchMyReservations(),
  ]);

  const handleFulfillReservation = async (reservationId: number) => {
    if (!user) return;
    setReservationActionId(reservationId);
    try {
      const response = await fulfillReservation(user.userId, reservationId, borrowDays);
      toast.success(response.message || "借書成功");
      await refreshDashboard();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "借閱失敗");
    } finally {
      setReservationActionId(null);
    }
  };

  const handleCancelReservation = async (reservationId: number) => {
    if (!user) return;
    setReservationActionId(reservationId);
    try {
      const response = await cancelReservation(user.userId, reservationId);
      toast.success(response.message || "已取消預約");
      await refreshDashboard();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "取消預約失敗");
    } finally {
      setReservationActionId(null);
    }
  };

  return <>
    <PageHeader title={`歡迎回來，${user?.name ?? ""}`} description={`學號 ${user?.studentId}｜身分 ${user?.level === "VIP" ? "VIP 使用者" : "一般使用者"}`} />
    <Card className="mb-4 border-amber-300 bg-amber-50"><CardContent className="p-4"><p className="mb-2 text-sm font-semibold text-amber-800">預約到書通知</p>{notifiedReservations.length > 0 ? <div className="space-y-2">{notifiedReservations.map((r)=><div key={r.reservationId} className="flex flex-wrap items-center justify-between gap-2 rounded-md border border-amber-200 bg-white/60 p-3"><div><p className="text-sm font-medium text-amber-900">《{r.title}》可借閱</p><p className="text-xs text-amber-700">請於 {r.expiresAt ? formatDate(r.expiresAt) : "到期前"} 前借閱</p></div><Button className={bookActionClass("borrow")} disabled={reservationActionId===r.reservationId} onClick={()=>handleFulfillReservation(r.reservationId)}>{reservationActionId===r.reservationId?<Loader2 className="mr-1 h-4 w-4 animate-spin"/>:<BookPlus className="mr-1 h-4 w-4"/>}立即借閱</Button></div>)}</div> : <p className="text-sm text-amber-800/80">目前沒有可借閱的預約通知。</p>}</CardContent></Card>
    <Card className="mb-4 border-blue-300 bg-blue-50"><CardContent className="p-4"><p className="mb-2 text-sm font-semibold text-blue-800">我的預約中書籍</p>{waitingReservations.length > 0 ? <div className="space-y-2">{waitingReservations.map((r)=><div key={r.reservationId} className="flex flex-wrap items-center justify-between gap-2 rounded-md border border-blue-200 bg-white/60 p-3"><div><p className="text-sm font-medium text-blue-900">《{r.title}》</p><p className="text-xs text-blue-700">預約中{r.queuePosition ? `｜目前第 ${r.queuePosition} 位` : ""}</p></div><Button variant="outline" className={`${bookActionClass("disabled")} border-red-300 text-red-700 hover:bg-red-50 hover:text-red-800`} disabled={reservationActionId===r.reservationId} onClick={()=>handleCancelReservation(r.reservationId)}>{reservationActionId===r.reservationId?<Loader2 className="mr-1 h-4 w-4 animate-spin"/>:<X className="mr-1 h-4 w-4"/>}取消預約</Button></div>)}</div> : <p className="text-sm text-blue-800/80">目前沒有預約中的書籍。</p>}</CardContent></Card>

    {loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={refetch} /> : <div className="grid gap-5 lg:grid-cols-3">
      <Card className="lg:col-span-2"><CardContent className="p-6"><div className="mb-4 flex items-center justify-between"><h2 className="text-lg font-semibold">借閱中的書籍</h2><Link to="/user/records" className="text-sm text-primary hover:underline">查看全部 →</Link></div>{active.length === 0 ? <div className="rounded-xl border border-dashed p-6 text-center"><p>目前沒有借閱中的書籍</p><Button asChild><Link to="/user/books">查詢書籍</Link></Button></div> : <ul className="divide-y">{active.slice(0,5).map((r)=>{const remaining=daysUntil(r.dueDate); const isOverdue=r.status==="OVERDUE"||remaining<0; const soon=!isOverdue&&remaining<=3; return <li key={r.id} className="flex items-center justify-between gap-3 py-3"><div><p className="font-medium">{r.bookTitle}</p><p className="text-xs text-muted-foreground">借閱：{formatDate(r.borrowDate)}｜到期：{formatDate(r.dueDate)}</p><p className={`mt-1 text-xs font-medium ${isOverdue?"text-red-600":soon?"text-amber-600":"text-emerald-700"}`}>{isOverdue?`已逾期 ${Math.abs(remaining)} 天`:soon?`即將到期（${dueSoonText(r.dueDate)}）`:"尚未到期"}</p></div><StatusBadge status={isOverdue?"OVERDUE":r.status} /></li>})}</ul>}</CardContent></Card>
      <Card><CardContent className="p-6"><h2 className="mb-3 text-lg font-semibold">逾期 / 即將到期提醒</h2><div className="grid gap-3 md:grid-cols-2"><div className="rounded-md border border-red-300 bg-red-50 p-3"><p className="mb-2 text-sm font-semibold text-red-700">逾期提醒</p>{overdue.length===0?<p className="text-sm text-red-700/80">目前沒有逾期書籍。</p>:overdue.map((r)=><p key={r.id} className="text-sm text-red-700">{r.bookTitle}（已逾期 {Math.abs(daysUntil(r.dueDate))} 天）</p>)}</div><div className="rounded-md border border-yellow-300 bg-yellow-50 p-3"><p className="mb-2 text-sm font-semibold text-yellow-700">即將到期提醒</p>{dueSoon.length===0?<p className="text-sm text-yellow-700/80">目前沒有即將到期書籍。</p>:dueSoon.map((r)=><p key={r.id} className="text-sm text-yellow-700">{r.bookTitle}（{dueSoonText(r.dueDate)}）</p>)}</div></div></CardContent></Card>
      <Card className="lg:col-span-3"><CardContent className="p-6"><div className="mb-3 flex items-center justify-between"><h2 className="text-lg font-semibold">熱門書籍</h2><div className="space-x-2"><Button size="sm" variant={rankMode==="BORROW"?"default":"outline"} onClick={()=>setRankMode("BORROW")}>借閱最多</Button><Button size="sm" variant={rankMode==="RATING"?"default":"outline"} onClick={()=>setRankMode("RATING")}>評論高分</Button></div></div><ul className="space-y-2">{popularBooks.map((b,idx)=>{const reservation=reservationByBookId.get(b.bookId); const action=getBookAction(b.status,activeBorrowedBookIds.has(Number(b.bookId)),reservation?.status,reservation?.canBorrowNotified); return <li key={b.title} className="flex flex-wrap items-center justify-between gap-3 rounded border p-3 text-sm"><div><span className="mr-2 font-semibold">#{idx+1}</span>{b.title}<span className="ml-2 text-muted-foreground">借閱 {b.borrowCount} 次｜評分 {Number(b.avgRating).toFixed(1)}（{b.reviewCount} 筆）</span></div><Button size="sm" variant={action.tone==="reserve"?"secondary":"default"} className={bookActionClass(action.tone)} disabled={action.disabled} onClick={()=>showPopularDetail(b.bookId,b.title,b.status)}><BookPlus className="mr-1 h-4 w-4"/>{action.label}</Button></li>})}</ul>{popularBooks.length===0 && <p className="text-sm text-muted-foreground">目前查無熱門書籍資料，請先產生借閱/書評紀錄。</p>}</CardContent></Card>
      <QuickAction to="/user/books" icon={BookOpen} title="查詢書籍" desc="搜尋館藏並借閱" />
      <QuickAction to="/user/records" icon={ClipboardList} title="我的借閱紀錄" desc="查看與歸還" />
      <QuickAction to="/user/favorites" icon={Heart} title="我的收藏" desc="查看收藏書單" />
      <QuickAction to="/user/reviews" icon={MessageSquare} title="書評專區" desc="查看與撰寫書評" />
    </div>}

    <BookDetailDialog
      book={detail}
      user={user}
      open={!!detail}
      onOpenChange={(open) => {
        if (!open) {
          setDetail(null);
          void refreshDashboard();
        }
      }}
      onUpdated={refreshDashboard}
    />
  </>;
}

function QuickAction({ to, icon: Icon, title, desc }: { to: "/user"|"/user/books"|"/user/records"|"/user/favorites"|"/user/reviews"; icon: typeof BookOpen; title: string; desc: string; }) {
  return <Card><CardContent className="flex items-center gap-4 p-6"><Icon className="h-5 w-5" /><div className="flex-1"><p className="font-medium">{title}</p><p className="text-xs">{desc}</p></div><Button asChild variant="ghost" size="sm"><Link to={to}>前往</Link></Button></CardContent></Card>;
}
