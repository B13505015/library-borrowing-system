import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useMemo, useState } from "react";
import { Star } from "lucide-react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { useAuth } from "@/context/AuthContext";
import { addReview, getBookReviews } from "@/services/reviewService";
import { getMyBorrowRecords } from "@/services/borrowService";
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
  const [allBookReviews, setAllBookReviews] = useState<Array<{ bookId: string; bookTitle: string; review: string }>>([]);
  const [isLoadingAllReviews, setIsLoadingAllReviews] = useState(false);

  const loadBorrowedBooks = async () => {
    if (!user) return;
    const records = (await getMyBorrowRecords(user.studentId)).data ?? [];
    const titles = Array.from(new Set(records.map((r) => r.bookTitle).filter(Boolean)));
    const result: Book[] = [];
    for (const title of titles) {
      const found = (await searchBooks(title)).data?.find((b) => b.title === title);
      if (found) result.push(found);
    }
    setBorrowedBooks(result);
  };
  useEffect(() => {
    loadBorrowedBooks();
  }, [user?.userId]);



  const loadAllBookReviews = async () => {
    setIsLoadingAllReviews(true);
    try {
      const books = (await searchBooks("")).data ?? [];
      const reviewPairs = await Promise.all(
        books.map(async (book) => ({
          book,
          reviews: (await getBookReviews(Number(book.id))).data ?? [],
        })),
      );

      setAllBookReviews(
        reviewPairs.flatMap(({ book, reviews: bookReviews }) =>
          bookReviews.map((review) => ({ bookId: String(book.id), bookTitle: book.title, review })),
        ),
      );
    } finally {
      setIsLoadingAllReviews(false);
    }
  };

  useEffect(() => {
    if (!query.trim() || allBookReviews.length > 0) return;
    loadAllBookReviews();
  }, [query, allBookReviews.length]);

  const pickBook = async (id: string) => {
    const book = borrowedBooks.find((b) => String(b.id) === id) ?? null;
    setSelected(book);
    if (!book) return;
    setReviews((await getBookReviews(Number(book.id))).data ?? []);
  };

  const filteredReviews = useMemo(() => {
    const trimmed = query.trim().toLowerCase();
    if (!trimmed) {
      return reviews.map((review) => ({ bookId: String(selected?.id ?? ""), bookTitle: selected?.title ?? "", review }));
    }

    return allBookReviews.filter(({ bookTitle, review }) => {
      const normalized = `${bookTitle} ${review}`.toLowerCase();
      return normalized.includes(trimmed);
    });
  }, [allBookReviews, reviews, query, selected?.id, selected?.title]);

  const submit = async () => {
    if (!selected || !user) return;
    await addReview(Number(user.userId), Number(selected.id), rating, comment);
    setComment("");
    setReviews((await getBookReviews(Number(selected.id))).data ?? []);
    if (query.trim()) {
      await loadAllBookReviews();
    }
    toast.success("書評已送出");
  };


  return <><PageHeader title="書評專區" description="選擇借閱過的書，查看與撰寫書評" />
    <Card className="mb-4"><CardContent className="space-y-3 p-4">
      <select
        value={selected?.id ?? ""}
        onChange={(e) => pickBook(e.target.value)}
        className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
        disabled={borrowedBooks.length === 0}
      >
        <option value="">請選擇借閱過的書籍</option>
        {borrowedBooks.map((b) => <option key={b.id} value={b.id}>{b.title}</option>)}
      </select>
      {borrowedBooks.length === 0 && (
        <p className="text-sm text-muted-foreground">目前沒有借閱過的書籍可評論，請先借書後再來發表書評。</p>
      )}
      <Input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="查詢書籍評論（人名、書籍或評論關鍵字）" />
    </CardContent></Card>

    <Card><CardContent className="space-y-3 p-4">
      <div className="flex gap-1">{[1,2,3,4,5].map((n)=><button key={n} onClick={()=>setRating(n)}><Star className={`h-6 w-6 ${n<=rating?"fill-yellow-400 text-yellow-400":"text-muted-foreground"}`} /></button>)}</div>
      <Input value={comment} onChange={(e) => setComment(e.target.value)} placeholder="評論內容" />
      <div className="flex gap-2">
        <Button onClick={submit} disabled={!selected || !comment.trim()}>送出書評</Button>
              </div>
      {isLoadingAllReviews && query.trim() && <p className="text-sm text-muted-foreground">正在載入全書庫書評…</p>}
      {filteredReviews.map((item, i) => <p key={`${item.bookId}-${i}`} className="text-sm">[{item.bookTitle}] {item.review}</p>)}
    </CardContent></Card></>;
}
