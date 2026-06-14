import { useEffect, useState, type ReactNode } from "react";
import { BookPlus, Loader2 } from "lucide-react";
import { toast } from "sonner";
import { StatusBadge } from "@/components/common/StatusBadge";
import { Button } from "@/components/ui/button";
import {
  Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle,
} from "@/components/ui/dialog";
import { bookActionClass, getBookAction } from "@/lib/bookAction";
import { formatDate } from "@/lib/format";
import { borrowBook } from "@/services/borrowService";
import { getBookBorrowHistory, getBookDetail, getReservationInfo, type ReservationInfo } from "@/services/bookService";
import { fulfillReservation, reserveBook } from "@/services/reservationService";
import { getBookReviews } from "@/services/reviewService";
import type { Book } from "@/types/book";
import type { BorrowRecord } from "@/types/borrowRecord";
import type { AppUser } from "@/types/user";

type Props = {
  book: Book | null;
  user: AppUser | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onUpdated?: () => void | Promise<void>;
};

export function BookDetailDialog({ book, user, open, onOpenChange, onUpdated }: Props) {
  const [borrowDays, setBorrowDays] = useState<1 | 3 | 7 | 14>(7);
  const [history, setHistory] = useState<BorrowRecord[]>([]);
  const [reviews, setReviews] = useState<string[]>([]);
  const [reservationInfo, setReservationInfo] = useState<ReservationInfo | null>(null);
  const [latestBook, setLatestBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open || !book) return;
    setBorrowDays(user?.level === "VIP" ? 14 : 7);
    setLatestBook(null);
    setLoading(true);
    Promise.all([
      getBookDetail(book.id),
      getBookBorrowHistory(book.id),
      getBookReviews(Number(book.id)),
      user ? getReservationInfo(book.id, user.userId) : Promise.resolve(null),
    ]).then(([bookRes, historyRes, reviewRes, reservationRes]) => {
      setLatestBook(bookRes.data);
      setHistory(historyRes.data);
      setReviews(reviewRes.data);
      setReservationInfo(reservationRes?.data ?? null);
    }).catch(() => {
      setHistory([]);
      setReviews([]);
      setReservationInfo(null);
    }).finally(() => setLoading(false));
  }, [book, open, user]);

  if (!book) return null;
  const displayBook = latestBook ?? book;

  const action = getBookAction(
    displayBook.status,
    !!reservationInfo?.alreadyBorrowing,
    reservationInfo?.activeReservationStatus,
    reservationInfo?.canBorrowNotified ?? false,
  );
  const availableDays = user?.level === "VIP" ? [1, 3, 7, 14] : [1, 3, 7];

  const handleAction = async () => {
    if (!user) return;
    setSubmitting(true);
    try {
      if (
        reservationInfo?.activeReservationStatus === "NOTIFIED"
        && reservationInfo.canBorrowNotified
        && displayBook.status === "AVAILABLE"
        && reservationInfo.reservationId
      ) {
        await fulfillReservation(user.userId, reservationInfo.reservationId, borrowDays);
      } else if (displayBook.status === "BORROWED") {
        await reserveBook(user.userId, Number(displayBook.id));
      } else {
        await borrowBook(user.userId, Number(displayBook.id), borrowDays);
      }
      toast.success(action.label === "預約" ? "預約成功" : "借閱成功");
      await onUpdated?.();
      onOpenChange(false);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "操作失敗");
    } finally {
      setSubmitting(false);
    }
  };

  return <Dialog open={open} onOpenChange={onOpenChange}>
    <DialogContent className="max-h-[85vh] max-w-3xl overflow-hidden p-0">
      <div className="flex max-h-[85vh] flex-col">
        <div className="border-b bg-background px-6 py-4">
          <DialogHeader><DialogTitle className="truncate pr-8">{displayBook.title}</DialogTitle><DialogDescription>編號 {displayBook.id}</DialogDescription></DialogHeader>
        </div>
        <div className="flex-1 overflow-y-auto px-6 py-4">
          <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
            <div className="col-span-2"><dt className="text-xs text-muted-foreground">作者</dt><dd className="mt-1">{displayBook.authors || "—"}</dd></div>
            <div className="col-span-2"><dt className="text-xs text-muted-foreground">主題</dt><dd className="mt-1">{displayBook.subjects || "—"}</dd></div>
            <div className="col-span-2"><dt className="text-xs text-muted-foreground">ISBN</dt><dd className="mt-1">{displayBook.isbns.length > 0 ? displayBook.isbns.join("、") : "—"}</dd></div>
            <DT label="出版社" value={displayBook.publisher || "—"} /><DT label="出版年" value={displayBook.publishYear ? String(displayBook.publishYear) : "—"} />
            <DT label="版本" value={displayBook.edition || "—"} /><DT label="格式" value={displayBook.format || "—"} />
            <DT label="資料來源" value={displayBook.source || "—"} /><DT label="狀態" value={<StatusBadge status={displayBook.status} />} />
            <div className="col-span-2"><dt className="text-xs text-muted-foreground">附註</dt><dd className="mt-1">{displayBook.note || "—"}</dd></div>
          </dl>
          {reservationInfo && <div className="mt-3 rounded-md border border-amber-300 bg-amber-50 p-3 text-sm text-amber-800">
            目前預約人數：{reservationInfo.waitingCount} 人
            {reservationInfo.activeReservationStatus === "NOTIFIED" && reservationInfo.canBorrowNotified && displayBook.status === "AVAILABLE"
              ? "｜你的預約已到書"
              : reservationInfo.activeReservationStatus === "NOTIFIED"
                ? "｜書籍目前已借出，請重新預約或等待狀態更新"
                : reservationInfo.myQueuePosition
                  ? `｜你是第 ${reservationInfo.myQueuePosition} 位（已預約）`
                  : "｜你尚未在預約隊列"}
          </div>}
          <div className="mt-4"><label className="mb-2 block text-sm font-medium">租借期限</label><select value={borrowDays} onChange={(e)=>setBorrowDays(Number(e.target.value) as 1|3|7|14)} className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm">{availableDays.map((day)=><option key={day} value={day}>{day} 天</option>)}</select></div>
          <section className="mt-4"><h3 className="mb-2 text-sm font-semibold">近期借還紀錄</h3>{loading?<p className="text-sm text-muted-foreground">載入中...</p>:history.length===0?<p className="text-sm text-muted-foreground">目前沒有借還紀錄。</p>:<div className="max-h-56 overflow-y-auto rounded-md border"><table className="w-full text-sm"><tbody>{history.map((r)=><tr key={r.id} className="border-t"><td className="px-3 py-2">{r.studentName}</td><td className="px-3 py-2">{formatDate(r.borrowDate)}</td><td className="px-3 py-2">{formatDate(r.dueDate)}</td><td className="px-3 py-2"><StatusBadge status={r.status}/></td></tr>)}</tbody></table></div>}</section>
          <section className="mt-4"><h3 className="mb-2 text-sm font-semibold">書籍評論</h3>{loading?<p className="text-sm text-muted-foreground">載入中...</p>:reviews.length===0?<p className="text-sm text-muted-foreground">目前沒有評論。</p>:<div className="space-y-2">{reviews.map((review,index)=><p key={index} className="rounded-md border bg-muted/30 p-3 text-sm">{review}</p>)}</div>}</section>
        </div>
        <div className="border-t bg-background px-6 py-4"><DialogFooter><Button variant="outline" className="h-9 min-w-[96px] justify-center whitespace-nowrap px-3 text-sm" onClick={()=>onOpenChange(false)}>關閉</Button><Button className={bookActionClass(action.tone)} disabled={action.disabled||submitting||loading} onClick={handleAction}>{submitting?<Loader2 className="mr-1 h-4 w-4 animate-spin"/>:<BookPlus className="mr-1 h-4 w-4"/>}{action.label}</Button></DialogFooter></div>
      </div>
    </DialogContent>
  </Dialog>;
}

function DT({ label, value }: { label: string; value: ReactNode }) {
  return <div><dt className="text-xs text-muted-foreground">{label}</dt><dd className="mt-1">{value}</dd></div>;
}
