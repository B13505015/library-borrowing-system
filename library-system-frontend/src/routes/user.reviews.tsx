import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Star } from "lucide-react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { SearchBar } from "@/components/common/SearchBar";
import { useAuth } from "@/context/AuthContext";
import { addReview, getBookReviews } from "@/services/reviewService";
import { searchBooks } from "@/services/bookService";
import type { Book } from "@/types/book";

export const Route = createFileRoute("/user/reviews")({ component: Page });

function Page() {
  const { user } = useAuth();
  const [keyword, setKeyword] = useState("");
  const [books, setBooks] = useState<Book[]>([]);
  const [selected, setSelected] = useState<Book | null>(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [reviews, setReviews] = useState<string[]>([]);

  const doSearch = async (k: string) => {
    setKeyword(k);
    setBooks((await searchBooks(k)).data ?? []);
  };

  const pickBook = async (b: Book) => {
    setSelected(b);
    setReviews((await getBookReviews(Number(b.id))).data ?? []);
  };

  const submit = async () => {
    if (!selected) return;
    await addReview(Number(user!.userId), Number(selected.id), rating, comment);
    setComment("");
    setReviews((await getBookReviews(Number(selected.id))).data ?? []);
  };

  return <><PageHeader title="書評專區" description="用星等評論你借閱過的書" />
    <Card className="mb-4"><CardContent className="p-4"><SearchBar placeholder="搜尋書名" defaultValue={keyword} onSearch={doSearch} />
      <div className="mt-3 grid gap-2 md:grid-cols-2">{books.slice(0, 6).map((b)=><Button key={b.id} variant={selected?.id===b.id?"default":"outline"} onClick={()=>pickBook(b)} className="justify-start">{b.title}</Button>)}</div>
    </CardContent></Card>

    <Card><CardContent className="space-y-3 p-4"><p className="text-sm">目前選擇：{selected?.title ?? "尚未選擇書籍"}</p>
      <div className="flex gap-1">{[1,2,3,4,5].map((n)=><button key={n} onClick={()=>setRating(n)}><Star className={`h-6 w-6 ${n<=rating?"fill-yellow-400 text-yellow-400":"text-muted-foreground"}`} /></button>)}</div>
      <Input value={comment} onChange={(e) => setComment(e.target.value)} placeholder="評論內容" />
      <Button onClick={submit} disabled={!selected || !comment.trim()}>送出書評</Button>
      {reviews.map((r, i) => <p key={i} className="text-sm">{r}</p>)}
    </CardContent></Card></>;
}
