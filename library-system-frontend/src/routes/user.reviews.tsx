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
    if (!book) {
      setReviews([]);
      return;
    }
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


  const hasQuery = !!query.trim();
  const emptyMessage = hasQuery
    ? "找不到符合關鍵字的書評。"
    : selected
      ? "這本書目前尚無評論，成為第一位評論者。"
      : "目前尚無書評";

  return <><PageHeader title="書評專區" description="選擇借閱過的書，查看與撰寫書評" />
    <div className="space-y-6">
      <Card className="border-blue-100 bg-blue-50/30 shadow-sm"><CardContent className="space-y-4 p-5">
        <div>
          <h2 className="text-lg font-semibold">撰寫書評</h2>
          <p className="mt-1 text-sm text-muted-foreground">請先選擇你借閱過的書籍，再留下評分與評論</p>
        </div>
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
        <div>
          <p className="mb-2 text-sm font-medium">星等評分</p>
          <div className="flex gap-1">{[1,2,3,4,5].map((n)=><button type="button" key={n} onClick={()=>setRating(n)} aria-label={`${n} 星`}><Star className={`h-6 w-6 ${n<=rating?"fill-yellow-400 text-yellow-400":"text-muted-foreground"}`} /></button>)}</div>
        </div>
        <Input value={comment} onChange={(e) => setComment(e.target.value)} placeholder="評論內容" />
        <Button
          className="h-9 px-4 disabled:border disabled:border-gray-300 disabled:bg-gray-200 disabled:text-gray-500 disabled:opacity-100 disabled:shadow-none"
          onClick={submit}
          disabled={!selected || !comment.trim()}
        >
          送出書評
        </Button>
      </CardContent></Card>

      <Card className="border-blue-100 bg-blue-50/30 shadow-sm"><CardContent className="space-y-4 p-5">
        <div>
          <h2 className="text-lg font-semibold">查詢書評</h2>
          <p className="mt-1 text-sm text-muted-foreground">可依人名、書名或評論內容搜尋</p>
        </div>
        <Input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="搜尋人名、書名或評論關鍵字" />
        <div className="space-y-2">
          {isLoadingAllReviews && hasQuery && <p className="text-sm text-muted-foreground">正在載入全書庫書評…</p>}
          {!isLoadingAllReviews && filteredReviews.length === 0 && <p className="rounded-md border border-dashed bg-background/70 p-4 text-sm text-muted-foreground">{emptyMessage}</p>}
          {filteredReviews.map((item, i) => <p key={`${item.bookId}-${i}`} className="rounded-md border bg-background/80 p-3 text-sm">[{item.bookTitle}] {item.review}</p>)}
        </div>
      </CardContent></Card>
    </div>
  </>;
}
