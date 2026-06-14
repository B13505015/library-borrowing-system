import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { CircleCheck, ShieldCheck } from "lucide-react";
import { toast } from "sonner";
import { EmptyState } from "@/components/common/EmptyState";
import { ErrorState } from "@/components/common/ErrorState";
import { LoadingState } from "@/components/common/LoadingState";
import { SearchBar } from "@/components/common/SearchBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { useAsync } from "@/hooks/useAsync";
import { formatDate } from "@/lib/format";
import {
  getAdminPenalties,
  updatePenaltyStatus,
  type AdminPenalty,
  type AdminPenaltyFilter,
} from "@/services/adminPenaltyService";

export const Route = createFileRoute("/admin/penalties")({
  head: () => ({ meta: [{ title: "罰款管理 — 圖書館借還書系統" }] }),
  component: AdminPenaltiesPage,
});

function AdminPenaltiesPage() {
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState<AdminPenaltyFilter>("ALL");
  const [updatingId, setUpdatingId] = useState<number | null>(null);
  const { data, loading, error, refetch } = useAsync(
    () => getAdminPenalties(keyword, status).then((response) => response.data),
    [keyword, status],
  );

  const onUpdateStatus = async (penalty: AdminPenalty, nextStatus: "PAID" | "WAIVED") => {
    const action = nextStatus === "PAID" ? "標記為已繳" : "免除此筆罰款";
    if (!window.confirm(`確認要將「${penalty.userName}／${penalty.bookTitle}」${action}？`)) return;
    setUpdatingId(penalty.penaltyId);
    try {
      const response = await updatePenaltyStatus(penalty.penaltyId, nextStatus);
      toast.success(response.message || (nextStatus === "PAID" ? "已標記為已繳" : "罰款已免除"));
      await refetch();
    } catch (updateError) {
      toast.error(updateError instanceof Error ? updateError.message : "更新罰款狀態失敗");
    } finally {
      setUpdatingId(null);
    }
  };

  const hasFilter = keyword.trim() || status !== "ALL";

  return <>
    <PageHeader title="罰款管理" description="查看逾期罰款並管理繳費狀態。" />
    <Card className="mb-4 border-0 bg-card/80 shadow-sm">
      <CardContent className="flex flex-col gap-3 p-4 md:flex-row">
        <div className="flex-1">
          <SearchBar
            placeholder="搜尋使用者姓名、學號或書名..."
            defaultValue={keyword}
            onSearch={setKeyword}
          />
        </div>
        <Select value={status} onValueChange={(value) => setStatus(value as AdminPenaltyFilter)}>
          <SelectTrigger className="w-full md:w-[160px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">全部</SelectItem>
            <SelectItem value="UNPAID">未繳</SelectItem>
            <SelectItem value="PAID">已繳</SelectItem>
            <SelectItem value="WAIVED">已免除</SelectItem>
          </SelectContent>
        </Select>
      </CardContent>
    </Card>

    <Card className="border-0 bg-card/80 shadow-sm">
      <CardContent className="p-0">
        {loading ? <LoadingState /> : error ? (
          <ErrorState message={error} onRetry={refetch} />
        ) : !data || data.length === 0 ? (
          <div className="p-6">
            <EmptyState title={hasFilter ? "找不到符合條件的罰款紀錄" : "目前尚無罰款紀錄"} />
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[170px]">使用者</TableHead>
                <TableHead>書名</TableHead>
                <TableHead className="w-[110px]">借閱日期</TableHead>
                <TableHead className="w-[110px]">到期日</TableHead>
                <TableHead className="w-[110px]">歸還日</TableHead>
                <TableHead className="w-[90px]">逾期天數</TableHead>
                <TableHead className="w-[100px]">罰款金額</TableHead>
                <TableHead className="w-[90px]">狀態</TableHead>
                <TableHead className="w-[190px] text-right">操作</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.map((penalty) => (
                <TableRow key={penalty.penaltyId}>
                  <TableCell>
                    <p className="font-medium">{penalty.userName}</p>
                    <p className="font-mono text-xs text-muted-foreground">{penalty.studentId}</p>
                  </TableCell>
                  <TableCell className="font-medium">{penalty.bookTitle}</TableCell>
                  <TableCell>{formatDate(penalty.borrowDate)}</TableCell>
                  <TableCell>{formatDate(penalty.dueDate)}</TableCell>
                  <TableCell>{formatDate(penalty.returnDate)}</TableCell>
                  <TableCell>{penalty.overdueDays} 天</TableCell>
                  <TableCell>NT${penalty.amount.toFixed(2)}</TableCell>
                  <TableCell>
                    <PenaltyStatusBadge status={penalty.status} />
                  </TableCell>
                  <TableCell className="text-right">
                    {penalty.status === "OPEN" ? (
                      <div className="flex justify-end gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          disabled={updatingId === penalty.penaltyId}
                          onClick={() => onUpdateStatus(penalty, "PAID")}
                        >
                          <CircleCheck className="mr-1 h-4 w-4" />
                          標記已繳
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          disabled={updatingId === penalty.penaltyId}
                          onClick={() => onUpdateStatus(penalty, "WAIVED")}
                        >
                          <ShieldCheck className="mr-1 h-4 w-4" />
                          免除罰款
                        </Button>
                      </div>
                    ) : (
                      <span className="text-xs text-muted-foreground">
                        {penalty.status === "PAID" ? "已繳" : "已免除"}
                      </span>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>
  </>;
}

function PenaltyStatusBadge({ status }: { status: AdminPenalty["status"] }) {
  const label = status === "OPEN" ? "未繳" : status === "PAID" ? "已繳" : "已免除";
  const className = status === "OPEN"
    ? "border-destructive/30 bg-destructive/10 text-destructive"
    : status === "PAID"
      ? "border-success/30 bg-success/10 text-success"
      : "border-border bg-muted text-muted-foreground";
  return <Badge variant="outline" className={className}>{label}</Badge>;
}
