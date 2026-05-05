import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { useAuth } from "@/context/AuthContext";
import { addReview, getBookReviews } from "@/services/reviewService";

export const Route = createFileRoute("/user/reviews")({ component: Page });

function Page() {
  const { user } = useAuth();
  const [bookId, setBookId] = useState("1");
  const [rating, setRating] = useState("5");
  const [comment, setComment] = useState("");
  const [reviews, setReviews] = useState<string[]>([]);

  const load = async () => setReviews((await getBookReviews(Number(bookId))).data ?? []);
  const submit = async () => {
    await addReview(Number(user!.userId), Number(bookId), Number(rating), comment);
    setComment("");
    await load();
  };

  return <><PageHeader title="書評專區" description="新增與查看書評" />
    <Card><CardContent className="space-y-2 p-4"><Input value={bookId} onChange={(e) => setBookId(e.target.value)} placeholder="bookId" />
    <Input value={rating} onChange={(e) => setRating(e.target.value)} placeholder="rating 1-5" />
    <Input value={comment} onChange={(e) => setComment(e.target.value)} placeholder="評論內容" />
    <div className="flex gap-2"><Button onClick={submit}>送出書評</Button><Button variant="outline" onClick={load}>載入書評</Button></div>
    {reviews.map((r, i) => <p key={i} className="text-sm">{r}</p>)}
    </CardContent></Card></>;
}
