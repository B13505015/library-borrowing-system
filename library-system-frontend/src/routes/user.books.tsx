import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { Eye, BookPlus, Heart } from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/layout/PageHeader";
import { SearchBar } from "@/components/common/SearchBar";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { EmptyState } from "@/components/common/EmptyState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useAsync } from "@/hooks/useAsync";
import { getAllBooks, searchBooks, getMyReservations, type MyReservation } from "@/services/bookService";
import { getMyBorrowRecords } from "@/services/borrowService";
import { addFavorite, getMyFavoriteBookIds, removeFavorite } from "@/services/favoriteService";
import { useAuth } from "@/context/AuthContext";
import type { Book } from "@/types/book";
import { bookActionClass, getBookAction } from "@/lib/bookAction";
import { getActiveBorrowedBookIds } from "@/lib/borrowRecord";
import { BookDetailDialog } from "@/components/books/BookDetailDialog";

export const Route = createFileRoute("/user/books")({
  head: () => ({ meta: [{ title: "查詢書籍 — 圖書館借還書系統" }] }),
  component: SearchBooksPage,
});

function SearchBooksPage() {
  const { user } = useAuth();
  const [keyword, setKeyword] = useState("");
  const [detail, setDetail] = useState<Book | null>(null);
  const [favoriteIds, setFavoriteIds] = useState<Set<number>>(new Set());
  const [activeBorrowedBookIds, setActiveBorrowedBookIds] = useState<Set<number>>(new Set());
  const [reservationsByBookId, setReservationsByBookId] = useState<Map<number, MyReservation>>(new Map());

  const { data, loading, error, refetch } = useAsync(
    () => searchBooks(keyword).then((r) => r.data),
    [keyword],
  );

  useEffect(() => {
    const loadFavorites = async () => {
      if (!user) return;
      try {
        const res = await getMyFavoriteBookIds(user.userId);
        setFavoriteIds(new Set(res.data));
      } catch {}
    };
    const loadMyActiveBorrows = async () => {
      if (!user) return;
      try {
        const [res, books] = await Promise.all([
          getMyBorrowRecords(user.studentId),
          getAllBooks(),
        ]);
        setActiveBorrowedBookIds(getActiveBorrowedBookIds(res.data ?? [], books.data ?? []));
      } catch {}
    };
    const loadReservationState = async () => {
      if (!user) return;
      try {
        const res = await getMyReservations(user.userId);
        setReservationsByBookId(new Map(res.data.map((r) => [r.bookId, r])));
      } catch {}
    };
    loadFavorites();
    loadMyActiveBorrows();
    loadReservationState();
  }, [user]);

  const toggleFavorite = async (book: Book) => {
    if (!user) return;
    const bookId = Number(book.id);
    try {
      if (favoriteIds.has(bookId)) {
        await removeFavorite(user.userId, bookId);
        const next = new Set(favoriteIds);
        next.delete(bookId);
        setFavoriteIds(next);
        toast.success("已移除收藏");
      } else {
        await addFavorite(user.userId, bookId);
        const next = new Set(favoriteIds);
        next.add(bookId);
        setFavoriteIds(next);
        toast.success("已加入收藏");
      }
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "收藏操作失敗");
    }
  };

  const refreshUserBookState = async () => {
    if (!user) return;
    const [borrowRes, reservationRes, booksRes] = await Promise.all([
      getMyBorrowRecords(user.studentId),
      getMyReservations(user.userId),
      getAllBooks(),
      refetch(),
    ]);
    setActiveBorrowedBookIds(getActiveBorrowedBookIds(borrowRes.data ?? [], booksRes.data ?? []));
    setReservationsByBookId(new Map(reservationRes.data.map((r) => [r.bookId, r])));
  };

  return (
    <>
      <PageHeader title="查詢書籍" description="輸入關鍵字搜尋書名、出版社或書籍編號。" />

      <Card className="mb-4">
        <CardContent className="p-4">
          <SearchBar
            placeholder="搜尋書名、出版社、編號..."
            defaultValue={keyword}
            onSearch={setKeyword}
          />
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-0">
          {loading ? (
            <LoadingState />
          ) : error ? (
            <ErrorState message={error} onRetry={refetch} />
          ) : !data || data.length === 0 ? (
            <EmptyState title="找不到符合的書籍" description="請嘗試其他關鍵字。" />
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[100px]">編號</TableHead>
                  <TableHead>書名</TableHead>
                  <TableHead>出版社</TableHead>
                  <TableHead className="w-[100px]">出版年</TableHead>
                  <TableHead className="w-[100px]">狀態</TableHead>
                  <TableHead className="w-[200px] text-right">操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {data.map((b) => {
                  const action = getBookAction(
                    b.status,
                    activeBorrowedBookIds.has(Number(b.id)),
                    reservationsByBookId.get(Number(b.id))?.status,
                  );
                  return (
                  <TableRow key={b.id}>
                    <TableCell className="font-mono text-xs">{b.id}</TableCell>
                    <TableCell className="font-medium">{b.title}</TableCell>
                    <TableCell>{b.publisher}</TableCell>
                    <TableCell>{b.publishYear}</TableCell>
                    <TableCell>
                      <StatusBadge status={b.status} />
                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="sm" onClick={() => setDetail(b)}>
                        <Eye className="mr-1 h-4 w-4" />
                        詳情
                      </Button>

                      <Button variant="ghost" size="sm" className="ml-2" onClick={() => toggleFavorite(b)}>
                        <Heart className={`mr-1 h-4 w-4 ${favoriteIds.has(Number(b.id)) ? "fill-current text-rose-500" : ""}`} />
                        收藏
                      </Button>

                      <Button
                        variant={action.tone === "reserve" ? "secondary" : "default"}
                        size="sm"
                        className={`ml-2 ${bookActionClass(action.tone)}`}
                        disabled={action.disabled}
                        onClick={() => setDetail(b)}
                      >
                        <BookPlus className="mr-1 h-4 w-4" />
                        {action.label}
                      </Button>
                    </TableCell>
                  </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      <BookDetailDialog book={detail} user={user} open={!!detail} onOpenChange={(open)=>!open&&setDetail(null)} onUpdated={refreshUserBookState} />
    </>
  );
}
