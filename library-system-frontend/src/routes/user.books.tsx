import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState, type ReactNode } from "react";
import { Eye, BookPlus, Loader2, Heart } from "lucide-react";
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
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { useAsync } from "@/hooks/useAsync";
import { searchBooks, getBookBorrowHistory } from "@/services/bookService";
import { borrowBook } from "@/services/borrowService";
import { addFavorite, getMyFavoriteBookIds, removeFavorite } from "@/services/favoriteService";
import { useAuth } from "@/context/AuthContext";
import type { Book } from "@/types/book";
import type { BorrowRecord } from "@/types/borrowRecord";
import { formatDate } from "@/lib/format";

export const Route = createFileRoute("/user/books")({
  head: () => ({ meta: [{ title: "查詢書籍 — 圖書館借還書系統" }] }),
  component: SearchBooksPage,
});

function SearchBooksPage() {
  const { user } = useAuth();
  const [keyword, setKeyword] = useState("");
  const [detail, setDetail] = useState<Book | null>(null);
  const [borrowingId, setBorrowingId] = useState<string | null>(null);
  const [borrowDays, setBorrowDays] = useState<1 | 3 | 7 | 14>(7);
  const [history, setHistory] = useState<BorrowRecord[]>([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [favoriteIds, setFavoriteIds] = useState<Set<number>>(new Set());

  const { data, loading, error, refetch } = useAsync(
    () => searchBooks(keyword).then((r) => r.data),
    [keyword],
  );

  const availableBorrowDays = user?.level === "VIP" ? [1, 3, 7, 14] : [1, 3, 7];

  useEffect(() => {
    const loadFavorites = async () => {
      if (!user) return;
      try {
        const res = await getMyFavoriteBookIds(user.userId);
        setFavoriteIds(new Set(res.data));
      } catch {}
    };
    loadFavorites();
  }, [user]);

  const handleBorrow = async (book: Book) => {
    if (!user) return;
    setBorrowingId(book.id);

    try {
      await borrowBook(user.userId, Number(book.id), borrowDays);
      toast.success(`已成功借閱《${book.title}》${borrowDays} 天`);
      setDetail(null);
      setBorrowDays(7);
      setHistory([]);
      refetch();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "借閱失敗");
    } finally {
      setBorrowingId(null);
    }
  };



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

  const showBookDetail = async (book: Book) => {
    setBorrowDays(user?.level === "VIP" ? 14 : 7);
    setDetail(book);
    setHistory([]);
    setHistoryLoading(true);

    try {
      const res = await getBookBorrowHistory(book.id);
      setHistory(res.data);
    } catch {
      setHistory([]);
    } finally {
      setHistoryLoading(false);
    }
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
                {data.map((b) => (
                  <TableRow key={b.id}>
                    <TableCell className="font-mono text-xs">{b.id}</TableCell>
                    <TableCell className="font-medium">{b.title}</TableCell>
                    <TableCell>{b.publisher}</TableCell>
                    <TableCell>{b.publishYear}</TableCell>
                    <TableCell>
                      <StatusBadge status={b.status} />
                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="sm" onClick={() => showBookDetail(b)}>
                        <Eye className="mr-1 h-4 w-4" />
                        詳情
                      </Button>

                      <Button variant="ghost" size="sm" className="ml-2" onClick={() => toggleFavorite(b)}>
                        <Heart className={`mr-1 h-4 w-4 ${favoriteIds.has(Number(b.id)) ? "fill-current text-rose-500" : ""}`} />
                        收藏
                      </Button>

                      <Button
                        variant="default"
                        size="sm"
                        className="ml-2"
                        disabled={b.status !== "AVAILABLE" || borrowingId === b.id}
                        onClick={() => showBookDetail(b)}
                      >
                        <BookPlus className="mr-1 h-4 w-4" />
                        借閱
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      <Dialog
        open={!!detail}
        onOpenChange={(o) => {
          if (!o) {
            setDetail(null);
            setBorrowDays(7);
            setHistory([]);
          }
        }}
      >
        <DialogContent className="max-h-[85vh] max-w-3xl overflow-hidden p-0">
          {detail && (
            <div className="flex max-h-[85vh] flex-col">
              <div className="border-b bg-background px-6 py-4">
                <DialogHeader>
                  <DialogTitle className="truncate pr-8">{detail.title}</DialogTitle>
                  <DialogDescription>編號 {detail.id}</DialogDescription>
                </DialogHeader>
              </div>

              <div className="flex-1 overflow-y-auto px-6 py-4">
                <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
                  <DT label="出版社" value={detail.publisher} />
                  <DT label="出版年" value={String(detail.publishYear)} />
                  <DT label="版本" value={detail.edition || "—"} />
                  <DT label="格式" value={detail.format || "—"} />
                  <DT label="資料來源" value={detail.source || "—"} />
                  <DT label="狀態" value={<StatusBadge status={detail.status} />} />
                  <div className="col-span-2">
                    <dt className="text-xs text-muted-foreground">附註</dt>
                    <dd className="mt-1">{detail.note || "—"}</dd>
                  </div>
                </dl>

                <div className="mt-4">
                  <label className="mb-2 block text-sm font-medium">租借期限</label>
                  <select
                    value={borrowDays}
                    onChange={(e) => setBorrowDays(Number(e.target.value) as 1 | 3 | 7 | 14)}
                    className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                  >
                    {availableBorrowDays.map((day) => (
                      <option key={day} value={day}>
                        {day} 天
                      </option>
                    ))}
                  </select>
                  <p className="mt-2 text-xs text-muted-foreground">
                    {user?.level === "VIP"
                      ? "VIP 使用者可借閱最長 14 天"
                      : "一般使用者可借閱最長 7 天"}
                  </p>
                </div>

                <div className="mt-4">
                  <h3 className="mb-2 text-sm font-semibold">近期借還紀錄</h3>

                  {historyLoading ? (
                    <p className="text-sm text-muted-foreground">載入中...</p>
                  ) : history.length === 0 ? (
                    <p className="text-sm text-muted-foreground">目前沒有借還紀錄。</p>
                  ) : (
                    <div className="max-h-64 overflow-y-auto rounded-md border">
                      <table className="w-full text-sm">
                        <thead className="sticky top-0 bg-muted/50">
                          <tr>
                            <th className="px-3 py-2 text-left">學號</th>
                            <th className="px-3 py-2 text-left">姓名</th>
                            <th className="px-3 py-2 text-left">借出時間</th>
                            <th className="px-3 py-2 text-left">到期時間</th>
                            <th className="px-3 py-2 text-left">歸還時間</th>
                            <th className="px-3 py-2 text-left">狀態</th>
                          </tr>
                        </thead>
                        <tbody>
                          {history.map((r) => (
                            <tr key={r.id} className="border-t">
                              <td className="px-3 py-2">{r.studentId}</td>
                              <td className="px-3 py-2">{r.studentName}</td>
                              <td className="px-3 py-2">{formatDate(r.borrowDate)}</td>
                              <td className="px-3 py-2">{formatDate(r.dueDate)}</td>
                              <td className="px-3 py-2">{formatDate(r.returnDate)}</td>
                              <td className="px-3 py-2">
                                <StatusBadge status={r.status} />
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              </div>

              <div className="border-t bg-background px-6 py-4">
                <DialogFooter>
                  <Button variant="outline" onClick={() => setDetail(null)}>
                    關閉
                  </Button>
                  <Button
                    disabled={detail.status !== "AVAILABLE" || borrowingId === detail.id}
                    onClick={() => handleBorrow(detail)}
                  >
                    {borrowingId === detail.id ? (
                      <Loader2 className="mr-1 h-4 w-4 animate-spin" />
                    ) : (
                      <BookPlus className="mr-1 h-4 w-4" />
                    )}
                    借閱此書
                  </Button>
                </DialogFooter>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </>
  );
}

function DT({ label, value }: { label: string; value: ReactNode }) {
  return (
    <div>
      <dt className="text-xs text-muted-foreground">{label}</dt>
      <dd className="mt-1">{value}</dd>
    </div>
  );
}