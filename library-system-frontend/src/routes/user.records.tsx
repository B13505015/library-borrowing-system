import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Loader2, Undo2 } from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/layout/PageHeader";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { EmptyState } from "@/components/common/EmptyState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useAsync } from "@/hooks/useAsync";
import { useAuth } from "@/context/AuthContext";
import { getMyBorrowRecords, handleReturnBook } from "@/services/borrowService";
import { formatDate } from "@/lib/format";

export const Route = createFileRoute("/user/records")({
  head: () => ({ meta: [{ title: "我的借閱紀錄 — 圖書館借還書系統" }] }),
  component: BorrowRecordsPage,
});

function BorrowRecordsPage() {
  const { user } = useAuth();
  const [returningId, setReturningId] = useState<string | null>(null);

  // renderBorrowRecords
  const { data, loading, error, refetch } = useAsync(
    () => getMyBorrowRecords(user!.studentId).then((r) => r.data),
    [user?.studentId],
  );

  const onReturn = async (id: string) => {
    setReturningId(id);
    try {
      await handleReturnBook(id);
      toast.success("歸還成功");
      refetch();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "歸還失敗");
    } finally {
      setReturningId(null);
    }
  };

    const sortedRecords = [...(data ?? [])].sort((a, b) =>
    String(b.borrowDate ?? "").localeCompare(String(a.borrowDate ?? "")),
  );

  return (
    <>
      <PageHeader title="我的借閱紀錄" description="顯示您所有的借閱與歸還紀錄。" />
      <Card>
        <CardContent className="p-0">
          {loading ? (
            <LoadingState />
          ) : error ? (
            <ErrorState message={error} onRetry={refetch} />
          ) : !data || data.length === 0 ? (
            <EmptyState title="尚無借閱紀錄" description="您還沒有任何借閱紀錄。" />
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>書名</TableHead>
                  <TableHead className="w-[120px]">借書日期</TableHead>
                  <TableHead className="w-[120px]">到期日期</TableHead>
                  <TableHead className="w-[120px]">還書日期</TableHead>
                  <TableHead className="w-[100px]">狀態</TableHead>
                  <TableHead className="w-[120px] text-right">操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {sortedRecords.map((r) => (
                  <TableRow key={r.id} className={r.status === "OVERDUE" ? "bg-destructive/5" : ""}>
                    <TableCell className="font-medium">{r.bookTitle}</TableCell>
                    <TableCell>{formatDate(r.borrowDate)}</TableCell>
                    <TableCell className={r.status === "OVERDUE" ? "font-medium text-destructive" : ""}>
                      {formatDate(r.dueDate)}
                    </TableCell>
                    <TableCell>{formatDate(r.returnDate)}</TableCell>
                    <TableCell><StatusBadge status={r.status} /></TableCell>
                    <TableCell className="text-right">
                      {r.status !== "RETURNED" ? (
                        <Button
                          size="sm"
                          variant="outline"
                          disabled={returningId === r.id}
                          onClick={() => onReturn(r.id)}
                        >
                          {returningId === r.id ? (
                            <Loader2 className="mr-1 h-4 w-4 animate-spin" />
                          ) : (
                            <Undo2 className="mr-1 h-4 w-4" />
                          )}
                          還書
                        </Button>
                      ) : (
                        <span className="text-xs text-muted-foreground">—</span>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </>
  );
}
