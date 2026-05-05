import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/layout/PageHeader";
import { useAsync } from "@/hooks/useAsync";
import { useAuth } from "@/context/AuthContext";
import { getMyFavoriteBookIds } from "@/services/favoriteService";
import { getAllBooks } from "@/services/bookService";
import { Card, CardContent } from "@/components/ui/card";

export const Route = createFileRoute("/user/favorites")({ component: Page });

function Page() {
  const { user } = useAuth();
  const { data } = useAsync(async () => {
    const [fav, books] = await Promise.all([getMyFavoriteBookIds(Number(user!.userId)), getAllBooks()]);
    const ids = new Set(fav.data ?? []);
    return (books.data ?? []).filter((b) => ids.has(Number(b.id)));
  }, [user?.userId]);

  return <><PageHeader title="我的收藏" description="你收藏的書籍清單" />
    <div className="grid gap-3 md:grid-cols-2">{(data ?? []).map((b) => <Card key={b.id}><CardContent className="p-4">{b.title}</CardContent></Card>)}</div>
  </>;
}
