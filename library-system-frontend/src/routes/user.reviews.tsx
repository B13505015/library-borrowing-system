import { createFileRoute } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { Star } from "lucide-react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { useAuth } from "@/context/AuthContext";
import { addReview, getBookReviews } from "@/services/reviewService";
import { getMyBorrowRecords, borrowBook } from "@/services/borrowService";
import { searchBooks } from "@/services/bookService";
import type { Book } from "@/types/book";
import { toast } from "sonner";

export const Route = createFileRoute("/user/reviews")({ component: Page });

function Page() {
  const { user } = useAuth();
  const [borrowedBooks, setBorrowedBooks] = useState<Book[]>([]);
  const [selected, setSelected] = useState<Book | null>(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [reviews, setReviews] = useState<string[]>([]);
  const [query, setQuery] = useState("");

  const loadBorrowedBooks = async () => {
    if (!user) return;
    const records = (await getMyBorrowRecords(user.studentId)).data ?? [];
    const ids = Array.from(new Set(records.map((r) => String(r.bookId))));
    const result: Book[] = [];
    for (const id of ids) {
      const found = (await searchBooks(id)).data?.find((b) => b.id === id);
      if (found) result.push(found);
    }
    setBorrowedBooks(result);
  };

  const pickBook = async (id: string) => {
    const book = borrowedBooks.find((b) => b.id === id) ?? null;
    setSelected(book);
    if (!book) return;
    setReviews((await getBookReviews(Number(book.id))).data ?? []);
  };

  const filteredReviews = useMemo(() => {
    if (!query.trim()) return reviews;
    return reviews.filter((r) => r.toLowerCase().includes(query.toLowerCase()));
  }, [reviews, query]);

  const submit = async () => {
    if (!selected || !user) return;
    await addReview(Number(user.userId), Number(selected.id), rating, comment);
    setComment("");
    setReviews((await getBookReviews(Number(selected.id))).data ?? []);
    toast.success("書評已送出");
  };

  const handleBorrow = async () => {
    if (!selected || !user) return;
    await borrowBook(user.userId, Number(selected.id), 7);
    toast.success(`已借閱《${selected.title}》`);
  };

  return <><PageHeader title="書評專區" description="選擇借閱過的書，查看與撰寫書評" />
    <Card className="mb-4"><CardContent className="space-y-3 p-4">
      <Button variant="outline" onClick={loadBorrowedBooks}>載入我借閱過的書</Button>
      <select
        value={selected?.id ?? ""}
        onChange={(e) => pickBook(e.target.value)}
        className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
      >
        <option value="">請選擇借閱過的書籍</option>
        {borrowedBooks.map((b) => <option key={b.id} value={b.id}>{b.title}</option>)}
      </select>
      <Input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="查詢書籍評論（人名、書籍或評論關鍵字）" />
    </CardContent></Card>

    <Card><CardContent className="space-y-3 p-4"><p className="text-sm">目前選擇：{selected?.title ?? "尚未選擇書籍"}</p>
      <div className="flex gap-1">{[1,2,3,4,5].map((n)=><button key={n} onClick={()=>setRating(n)}><Star className={`h-6 w-6 ${n<=rating?"fill-yellow-400 text-yellow-400":"text-muted-foreground"}`} /></button>)}</div>
      <Input value={comment} onChange={(e) => setComment(e.target.value)} placeholder="評論內容" />
      <div className="flex gap-2">
        <Button onClick={submit} disabled={!selected || !comment.trim()}>送出書評</Button>
        <Button variant="secondary" onClick={handleBorrow} disabled={!selected}>借閱這本書</Button>
      </div>
      {filteredReviews.map((r, i) => <p key={i} className="text-sm">{r}</p>)}
    </CardContent></Card></>;
}
