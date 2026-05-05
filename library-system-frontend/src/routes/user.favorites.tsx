import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/layout/PageHeader";
import { useAsync } from "@/hooks/useAsync";
import { useAuth } from "@/context/AuthContext";
import { getMyFavoriteBookIds } from "@/services/favoriteService";
import { getAllBooks } from "@/services/bookService";
import { borrowBook } from "@/services/borrowService";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { StatusBadge } from "@/components/common/StatusBadge";
import { toast } from "sonner";

export const Route = createFileRoute("/user/favorites")({ component: Page });

function Page() {
  const { user } = useAuth();
  const borrowDays = user?.level === "VIP" ? 14 : 7;
  const { data } = useAsync(async () => {
    const [fav, books] = await Promise.all([getMyFavoriteBookIds(Number(user!.userId)), getAllBooks()]);
    const ids = new Set(fav.data ?? []);
    return (books.data ?? []).filter((b) => ids.has(Number(b.id)));
  }, [user?.userId]);

  const handleBorrow = async (bookId: number, title: string) => {
    if (!user) return;
    await borrowBook(user.userId, bookId, borrowDays);
    toast.success(`《${title}》${borrowDays} 天借閱 / 預約申請已送出`);
  };

  return <><PageHeader title="我的收藏" description="你收藏的書籍清單" />
    <div className="grid gap-3 md:grid-cols-2">{(data ?? []).map((b) => <Card key={b.id}><CardContent className="space-y-2 p-4">
      <div className="flex items-center justify-between"><h3 className="font-semibold">{b.title}</h3><StatusBadge status={b.status} /></div>
      <p className="text-sm text-muted-foreground">編號：{b.id}</p>
      <p className="text-sm text-muted-foreground">出版社：{b.publisher}</p>
      <p className="text-sm text-muted-foreground">出版年：{b.publishYear}</p>
      <p className="text-sm text-muted-foreground">格式：{b.format || "—"}</p>
      <div className="flex gap-2">
        <Button variant="outline" onClick={() => toast.info(`《${b.title}》詳情：${b.note || "無附註"}`)}>詳情</Button>
        <Button disabled={b.status === "REMOVED"} onClick={() => handleBorrow(Number(b.id), b.title)}>{b.status === "AVAILABLE" ? "借閱" : "預約"}</Button>
      </div>
    </CardContent></Card>)}</div>
  </>;
}
