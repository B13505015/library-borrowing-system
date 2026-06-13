import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { useAsync } from "@/hooks/useAsync";
import { useAuth } from "@/context/AuthContext";
import { getMyFavoriteBookIds } from "@/services/favoriteService";
import { getAllBooks, getMyReservations, type MyReservation } from "@/services/bookService";
import { getMyBorrowRecords } from "@/services/borrowService";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { StatusBadge } from "@/components/common/StatusBadge";
import { BookDetailDialog } from "@/components/books/BookDetailDialog";
import { bookActionClass, getBookAction } from "@/lib/bookAction";
import type { Book } from "@/types/book";

export const Route = createFileRoute("/user/favorites")({ component: Page });

function Page() {
  const { user } = useAuth();
  const [detail, setDetail] = useState<Book | null>(null);
  const [activeBorrowedBookIds, setActiveBorrowedBookIds] = useState<Set<number>>(new Set());
  const [reservationsByBookId, setReservationsByBookId] = useState<Map<number, MyReservation>>(new Map());
  const { data, refetch } = useAsync(async () => {
    const [fav, books] = await Promise.all([getMyFavoriteBookIds(Number(user!.userId)), getAllBooks()]);
    const ids = new Set(fav.data ?? []);
    return (books.data ?? []).filter((b) => ids.has(Number(b.id)));
  }, [user?.userId]);

  const refreshUserState = async () => {
    if (!user) return;
    const [borrowRes, reservationRes] = await Promise.all([
      getMyBorrowRecords(user.studentId),
      getMyReservations(user.userId),
      refetch(),
    ]);
    setActiveBorrowedBookIds(new Set((borrowRes.data ?? []).filter((r) => r.status !== "RETURNED").map((r) => Number(r.bookId))));
    setReservationsByBookId(new Map(reservationRes.data.map((r) => [r.bookId, r])));
  };

  useEffect(() => { refreshUserState(); }, [user?.userId]);

  return <><PageHeader title="我的收藏" description="你收藏的書籍清單" />
    <div className="grid gap-3 md:grid-cols-2">{(data ?? []).map((b) => {const action=getBookAction(b.status,activeBorrowedBookIds.has(Number(b.id)),reservationsByBookId.get(Number(b.id))?.status); return <Card key={b.id}><CardContent className="space-y-2 p-4">
      <div className="flex items-center justify-between"><h3 className="font-semibold">{b.title}</h3><StatusBadge status={b.status} /></div>
      <p className="text-sm text-muted-foreground">編號：{b.id}</p>
      <p className="text-sm text-muted-foreground">出版社：{b.publisher}</p>
      <p className="text-sm text-muted-foreground">出版年：{b.publishYear}</p>
      <p className="text-sm text-muted-foreground">格式：{b.format || "—"}</p>
      <div className="flex flex-wrap gap-2">
        <Button variant="outline" className="h-10 min-w-[112px] justify-center whitespace-nowrap" onClick={() => setDetail(b)}>詳情</Button>
        <Button className={bookActionClass(action.tone)} disabled={action.disabled} onClick={() => setDetail(b)}>{action.label}</Button>
      </div>
    </CardContent></Card>})}</div>
    <BookDetailDialog book={detail} user={user} open={!!detail} onOpenChange={(open)=>!open&&setDetail(null)} onUpdated={refreshUserState} />
  </>;
}
