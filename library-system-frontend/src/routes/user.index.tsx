import { createFileRoute, Link } from "@tanstack/react-router";
import { BookOpen, BookPlus, ClipboardList, Heart, MessageSquare, Loader2 } from "lucide-react";
import { useMemo, useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { PageHeader } from "@/components/layout/PageHeader";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { useAsync } from "@/hooks/useAsync";
import { useAuth } from "@/context/AuthContext";
import { borrowBook, getMyBorrowRecords } from "@/services/borrowService";
import { getAllBooks, getBookBorrowHistory, getPopularBooks, getReservationInfo, type ReservationInfo } from "@/services/bookService";
import { toast } from "sonner";
import { formatDate, daysUntil, isDueSoon, dueSoonText } from "@/lib/format";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import type { Book } from "@/types/book";
import type { BorrowRecord } from "@/types/borrowRecord";

export const Route = createFileRoute("/user/")({ component: UserDashboardPage });
type RankMode = "BORROW" | "RATING";

function UserDashboardPage() {
  const { user } = useAuth();
  const [rankMode, setRankMode] = useState<RankMode>("BORROW");
  const [detail, setDetail] = useState<Book | null>(null);
  const [history, setHistory] = useState<BorrowRecord[]>([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [borrowDays, setBorrowDays] = useState<1 | 3 | 7 | 14>(7);
  const [borrowingId, setBorrowingId] = useState<string | null>(null);
  const [reservationInfo, setReservationInfo] = useState<ReservationInfo | null>(null);

  const { data, loading, error, refetch } = useAsync(() => getMyBorrowRecords(user!.studentId).then((r) => r.data), [user?.studentId]);
  const { data: rankedBooks, refetch: refetchRanked } = useAsync(() => getPopularBooks(rankMode === "BORROW" ? "borrow" : "rating", 20).then((r) => r.data), [rankMode]);
  const { data: allBooks, refetch: refetchAllBooks } = useAsync(() => getAllBooks().then((r) => r.data), []);

  const availableBorrowDays = user?.level === "VIP" ? [1, 3, 7, 14] : [1, 3, 7];
  const active = (data ?? []).filter((r) => r.status !== "RETURNED");
  const overdue = (data ?? []).filter((r) => r.status === "OVERDUE");
  const dueSoon = active.filter((r) => r.status !== "OVERDUE" && isDueSoon(r.dueDate));

  const popularBooks = useMemo(() => {
    const list = rankedBooks ?? [];
    const statusByTitle = new Map<string, "AVAILABLE" | "BORROWED" | "REMOVED">();
    const bookIdByTitle = new Map<string, number>();
    const bookByTitle = new Map<string, Book>();
    for (const book of allBooks ?? []) {
      const id = Number(book.id);
      if (!Number.isFinite(id)) continue;
      const cur = statusByTitle.get(book.title);
      if (!cur || cur === "BORROWED") {
        statusByTitle.set(book.title, book.status);
        bookIdByTitle.set(book.title, id);
        bookByTitle.set(book.title, book);
      }
      if (book.status === "AVAILABLE") {
        statusByTitle.set(book.title, "AVAILABLE");
        bookIdByTitle.set(book.title, id);
        bookByTitle.set(book.title, book);
      }
    }
    const deduped = new Map<string, (typeof list)[number]>();
    for (const item of list) if (!deduped.has(item.title)) deduped.set(item.title, item);
    return Array.from(deduped.values()).slice(0, 5).map((item) => ({ ...item, status: statusByTitle.get(item.title) ?? "BORROWED", bookId: bookIdByTitle.get(item.title) ?? item.bookId, detail: bookByTitle.get(item.title) }));
  }, [allBooks, rankedBooks]);

  const showPopularDetail = async (bookId: number, title: string) => {
    const selected = (allBooks ?? []).find((b) => Number(b.id) === bookId) ?? (allBooks ?? []).find((b) => b.title === title);
    if (!selected) return;
    setDetail(selected);
    setBorrowDays(user?.level === "VIP" ? 14 : 7);
    setHistoryLoading(true);
    setHistory([]);
    setReservationInfo(null);
    try {
      const [historyRes, reservationRes] = await Promise.all([
        getBookBorrowHistory(selected.id),
        getReservationInfo(selected.id, user?.userId),
      ]);
      setHistory(historyRes.data);
      setReservationInfo(reservationRes.data);
    } finally {
      setHistoryLoading(false);
    }
  };

  const handleBorrowFromDetail = async () => {
    if (!user || !detail) return;
    setBorrowingId(detail.id);
    try {
      const response = await borrowBook(user.userId, Number(detail.id), borrowDays);
      toast.success(response.message || "借閱/預約成功");
      await Promise.all([refetch(), refetchRanked(), refetchAllBooks()]);
      const refreshed = await getAllBooks();
      const now = refreshed.data.find((b) => b.title === detail.title && b.status === "AVAILABLE") ?? refreshed.data.find((b) => b.title === detail.title) ?? detail;
      setDetail(now);
      const reservationRes = await getReservationInfo(now.id, user.userId);
      setReservationInfo(reservationRes.data);
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "借閱失敗");
    } finally {
      setBorrowingId(null);
    }
  };

  return <>
    <PageHeader title={`歡迎回來，${user?.name ?? ""}`} description={`學號 ${user?.studentId}｜身分 ${user?.level === "VIP" ? "VIP 使用者" : "一般使用者"}`} />
    {loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={refetch} /> : <div className="grid gap-5 lg:grid-cols-3">
      <Card className="lg:col-span-2"><CardContent className="p-6"><div className="mb-4 flex items-center justify-between"><h2 className="text-lg font-semibold">借閱中的書籍</h2><Link to="/user/records" className="text-sm text-primary hover:underline">查看全部 →</Link></div>{active.length === 0 ? <div className="rounded-xl border border-dashed p-6 text-center"><p>目前沒有借閱中的書籍</p><Button asChild><Link to="/user/books">查詢書籍</Link></Button></div> : <ul className="divide-y">{active.slice(0,5).map((r)=><li key={r.id} className="flex items-center justify-between py-3"><div><p className="font-medium">{r.bookTitle}</p><p className="text-xs">借閱：{formatDate(r.borrowDate)} 到期：{formatDate(r.dueDate)} {r.status !== "OVERDUE" && `（剩 ${daysUntil(r.dueDate)} 天）`}</p></div><StatusBadge status={r.status} /></li>)}</ul>}</CardContent></Card>
      <Card><CardContent className="p-6"><h2 className="mb-3 text-lg font-semibold">逾期 / 即將到期提醒</h2><div className="grid gap-3 md:grid-cols-2"><div className="rounded-md border border-red-300 bg-red-50 p-3"><p className="mb-2 text-sm font-semibold text-red-700">逾期提醒</p>{overdue.length===0?<p className="text-sm text-red-700/80">目前沒有逾期書籍。</p>:overdue.map((r)=><p key={r.id} className="text-sm text-red-700">{r.bookTitle}（已逾期 {Math.abs(daysUntil(r.dueDate))} 天）</p>)}</div><div className="rounded-md border border-yellow-300 bg-yellow-50 p-3"><p className="mb-2 text-sm font-semibold text-yellow-700">即將到期提醒</p>{dueSoon.length===0?<p className="text-sm text-yellow-700/80">目前沒有即將到期書籍。</p>:dueSoon.map((r)=><p key={r.id} className="text-sm text-yellow-700">{r.bookTitle}（{dueSoonText(r.dueDate)}）</p>)}</div></div></CardContent></Card>
      <Card className="lg:col-span-3"><CardContent className="p-6"><div className="mb-3 flex items-center justify-between"><h2 className="text-lg font-semibold">熱門書籍</h2><div className="space-x-2"><Button size="sm" variant={rankMode==="BORROW"?"default":"outline"} onClick={()=>setRankMode("BORROW")}>借閱最多</Button><Button size="sm" variant={rankMode==="RATING"?"default":"outline"} onClick={()=>setRankMode("RATING")}>評論高分</Button></div></div><ul className="space-y-2">{popularBooks.map((b,idx)=><li key={b.title} className="flex items-center justify-between rounded border p-3 text-sm"><div><span className="mr-2 font-semibold">#{idx+1}</span>{b.title}<span className="ml-2 text-muted-foreground">借閱 {b.borrowCount} 次｜評分 {Number(b.avgRating).toFixed(1)}（{b.reviewCount} 筆）</span></div><Button size="sm" variant={b.status === "AVAILABLE" ? "default" : "secondary"} className={b.status === "AVAILABLE" ? "" : "bg-amber-500 text-white hover:bg-amber-600"} onClick={()=>showPopularDetail(b.bookId, b.title)}><BookPlus className="mr-1 h-4 w-4" />{b.status === "AVAILABLE" ? "借閱" : "預約"}</Button></li>)}</ul>{popularBooks.length===0 && <p className="text-sm text-muted-foreground">目前查無熱門書籍資料，請先產生借閱/書評紀錄。</p>}</CardContent></Card>
      <QuickAction to="/user/books" icon={BookOpen} title="查詢書籍" desc="搜尋館藏並借閱" />
      <QuickAction to="/user/records" icon={ClipboardList} title="我的借閱紀錄" desc="查看與歸還" />
      <QuickAction to="/user/favorites" icon={Heart} title="我的收藏" desc="查看收藏書單" />
      <QuickAction to="/user/reviews" icon={MessageSquare} title="書評專區" desc="查看與撰寫書評" />
    </div>}

    <Dialog open={!!detail} onOpenChange={(o) => !o && setDetail(null)}>
      <DialogContent className="max-h-[85vh] max-w-3xl overflow-hidden p-0">
        {detail && <div className="flex max-h-[85vh] flex-col"><div className="border-b bg-background px-6 py-4"><DialogHeader><DialogTitle>{detail.title}</DialogTitle><DialogDescription>編號 {detail.id}</DialogDescription></DialogHeader></div><div className="flex-1 overflow-y-auto px-6 py-4"><p className="text-sm">狀態：<StatusBadge status={detail.status} /></p><div className="mt-3"><label className="mb-2 block text-sm font-medium">租借期限</label><select value={borrowDays} onChange={(e)=>setBorrowDays(Number(e.target.value) as 1|3|7|14)} className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm">{availableBorrowDays.map((d)=><option key={d} value={d}>{d} 天</option>)}</select></div>{detail.status === "BORROWED" && reservationInfo && <div className="mt-3 rounded border border-amber-300 bg-amber-50 p-3 text-sm text-amber-800">目前預約人數：{reservationInfo.waitingCount} 人{reservationInfo.myQueuePosition ? `｜你是第 ${reservationInfo.myQueuePosition} 位` : "｜你尚未在預約隊列"}</div>}<div className="mt-4"><h3 className="mb-2 text-sm font-semibold">近期借還紀錄</h3>{historyLoading ? <p className="text-sm text-muted-foreground">載入中...</p> : history.length===0 ? <p className="text-sm text-muted-foreground">目前沒有借還紀錄。</p> : <div className="max-h-64 overflow-y-auto rounded-md border"><table className="w-full text-sm"><tbody>{history.map((r)=><tr key={r.id} className="border-t"><td className="px-3 py-2">{r.studentName}</td><td className="px-3 py-2">{formatDate(r.borrowDate)}</td><td className="px-3 py-2"><StatusBadge status={r.status} /></td></tr>)}</tbody></table></div>}</div></div><div className="border-t bg-background px-6 py-4"><DialogFooter><Button variant="outline" onClick={()=>setDetail(null)}>關閉</Button><Button disabled={detail.status==="REMOVED" || borrowingId===detail.id} onClick={handleBorrowFromDetail}>{borrowingId===detail.id ? <Loader2 className="mr-1 h-4 w-4 animate-spin"/> : <BookPlus className="mr-1 h-4 w-4"/>}{detail.status === "AVAILABLE" ? "借閱此書" : detail.status === "BORROWED" ? "預約此書" : "此書不可借"}</Button></DialogFooter></div></div>}
      </DialogContent>
    </Dialog>
  </>;
}

function QuickAction({ to, icon: Icon, title, desc }: { to: "/user"|"/user/books"|"/user/records"|"/user/favorites"|"/user/reviews"; icon: typeof BookOpen; title: string; desc: string; }) {
  return <Card><CardContent className="flex items-center gap-4 p-6"><Icon className="h-5 w-5" /><div className="flex-1"><p className="font-medium">{title}</p><p className="text-xs">{desc}</p></div><Button asChild variant="ghost" size="sm"><Link to={to}>前往</Link></Button></CardContent></Card>;
}
