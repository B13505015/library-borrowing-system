import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Star, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/layout/PageHeader";
import { SearchBar } from "@/components/common/SearchBar";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { EmptyState } from "@/components/common/EmptyState";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { useAsync } from "@/hooks/useAsync";
import {
  deleteAdminReview,
  getAdminReviews,
  type AdminReview,
} from "@/services/adminReviewService";
import { formatDate } from "@/lib/format";

export const Route = createFileRoute("/admin/reviews")({
  head: () => ({ meta: [{ title: "書評管理 — 圖書館借還書系統" }] }),
  component: AdminReviewsPage,
});

function AdminReviewsPage() {
  const [keyword, setKeyword] = useState("");
  const [removing, setRemoving] = useState<AdminReview | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const { data, loading, error, refetch } = useAsync(
    () => getAdminReviews(keyword).then((response) => response.data),
    [keyword],
  );

  const onDelete = async () => {
    if (!removing) return;
    setDeletingId(removing.reviewId);
    try {
      const response = await deleteAdminReview(removing.reviewId);
      toast.success(response.message || "書評已刪除");
      setRemoving(null);
      await refetch();
    } catch (deleteError) {
      toast.error(deleteError instanceof Error ? deleteError.message : "刪除書評失敗");
    } finally {
      setDeletingId(null);
    }
  };

  const emptyTitle = keyword.trim() ? "找不到符合條件的書評" : "目前尚無書評";

  return <>
    <PageHeader title="書評管理" description="查看、搜尋與管理使用者書評。" />
    <Card className="mb-4 border-0 bg-card/80 shadow-sm">
      <CardContent className="p-4">
        <SearchBar
          placeholder="搜尋書名、使用者姓名、學號或評論內容..."
          defaultValue={keyword}
          onSearch={setKeyword}
        />
      </CardContent>
    </Card>
    <Card className="border-0 bg-card/80 shadow-sm">
      <CardContent className="p-0">
        {loading ? <LoadingState /> : error ? (
          <ErrorState message={error} onRetry={refetch} />
        ) : !data || data.length === 0 ? (
          <div className="p-6"><EmptyState title={emptyTitle} /></div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>書名</TableHead>
                <TableHead className="w-[180px]">使用者</TableHead>
                <TableHead className="w-[130px]">評分</TableHead>
                <TableHead>評論內容</TableHead>
                <TableHead className="w-[120px]">評論時間</TableHead>
                <TableHead className="w-[100px] text-right">操作</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.map((review) => (
                <TableRow key={review.reviewId}>
                  <TableCell className="font-medium">{review.bookTitle}</TableCell>
                  <TableCell>
                    <p>{review.userName}</p>
                    <p className="font-mono text-xs text-muted-foreground">{review.studentId}</p>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-1" aria-label={`${review.rating} 星`}>
                      <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                      <span>{review.rating}</span>
                    </div>
                  </TableCell>
                  <TableCell className="max-w-md whitespace-normal break-words">{review.content || "—"}</TableCell>
                  <TableCell>{formatDate(review.createdAt)}</TableCell>
                  <TableCell className="text-right">
                    <Button
                      variant="ghost"
                      size="sm"
                      className="text-destructive hover:text-destructive"
                      onClick={() => setRemoving(review)}
                    >
                      <Trash2 className="mr-1 h-4 w-4" />
                      刪除
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>

    <AlertDialog open={!!removing} onOpenChange={(open) => !open && setRemoving(null)}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>確定要刪除此書評？</AlertDialogTitle>
          <AlertDialogDescription>
            將刪除《{removing?.bookTitle}》的這筆評論，此操作無法復原。
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={deletingId !== null}>取消</AlertDialogCancel>
          <AlertDialogAction
            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            disabled={deletingId !== null}
            onClick={onDelete}
          >
            確認刪除
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </>;
}
