import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Ban, CheckCircle2 } from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/layout/PageHeader";
import { SearchBar } from "@/components/common/SearchBar";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { EmptyState } from "@/components/common/EmptyState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { formatDate } from "@/lib/format";
import { useAsync } from "@/hooks/useAsync";
import { searchUsers, handleSuspendUser, handleActivateUser, getUserDetail, type AdminUserDetail } from "@/services/adminService";

export const Route = createFileRoute("/admin/users")({
  head: () => ({ meta: [{ title: "使用者管理 — 圖書館借還書系統" }] }),
  component: AdminUsersPage,
});

function AdminUsersPage() {
  const [keyword, setKeyword] = useState("");
  const { data, loading, error, refetch } = useAsync(
    () => searchUsers(keyword).then((r) => r.data),
    [keyword],
  );
  const [actingId, setActingId] = useState<string | null>(null);
  const [detail, setDetail] = useState<AdminUserDetail | null>(null);


  const onViewDetail = async (studentId: string) => {
    try {
      const res = await getUserDetail(studentId);
      setDetail(res.data);
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "查詢詳情失敗");
    }
  };

  const onToggle = async (studentId: string, suspend: boolean) => {
    setActingId(studentId);
    try {
      if (suspend) await handleSuspendUser(studentId);
      else await handleActivateUser(studentId);
      toast.success(suspend ? "已停權" : "已復權");
      refetch();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "操作失敗");
    } finally {
      setActingId(null);
    }
  };

  return (
    <>
      <PageHeader title="使用者管理" description="管理使用者帳號狀態。" />
      <Card className="mb-4 border-0 bg-card/80 shadow-sm">
        <CardContent className="p-4">
          <SearchBar placeholder="搜尋學號或姓名..." defaultValue={keyword} onSearch={setKeyword} />
        </CardContent>
      </Card>
      <Card className="border-0 bg-card/80 shadow-sm">
        <CardContent className="p-0">
          {loading ? (
            <LoadingState />
          ) : error ? (
            <ErrorState message={error} onRetry={refetch} />
          ) : !data || data.length === 0 ? (
            <div className="p-6"><EmptyState title="找不到符合的使用者" /></div>
          ) : (
            // renderAdminUserTable
            (<Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[160px]">學號</TableHead>
                  <TableHead>姓名</TableHead>
                  <TableHead className="w-[120px]">等級</TableHead>
                  <TableHead className="w-[100px]">狀態</TableHead>
                  <TableHead className="w-[220px] text-right">操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {data.map((u) => (
                  <TableRow key={u.studentId}>
                    <TableCell className="font-mono text-xs">{u.studentId}</TableCell>
                    <TableCell className="font-medium">{u.name}</TableCell>
                    <TableCell>{u.level === "VIP" ? "VIP" : "一般"}</TableCell>
                    <TableCell><StatusBadge status={u.status} /></TableCell>
                    <TableCell className="text-right">
                      {u.status === "ACTIVE" ? (
                        <Button
                          variant="outline" size="sm"
                          className="text-destructive hover:text-destructive"
                          disabled={actingId === u.studentId}
                          onClick={() => onToggle(u.studentId, true)}
                        >
                          <Ban className="mr-1 h-4 w-4" />
                          停權
                        </Button>
                      ) : (
                        <Button
                          variant="outline" size="sm"
                          disabled={actingId === u.studentId}
                          onClick={() => onToggle(u.studentId, false)}
                        >
                          <CheckCircle2 className="mr-1 h-4 w-4" />
                          復權
                        </Button>
                      )}
                      <Button variant="ghost" size="sm" className="ml-1" onClick={() => onViewDetail(u.studentId)}>詳情</Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>)
          )}
        </CardContent>
      </Card>
      <Dialog open={!!detail} onOpenChange={(o) => !o && setDetail(null)}>
        <DialogContent className="max-w-5xl">
          <DialogHeader><DialogTitle>使用者詳情 {detail?.studentId}</DialogTitle></DialogHeader>
          {detail && <div className="space-y-4 text-sm">
            <div className="grid gap-2 rounded-md bg-muted/40 p-3 sm:grid-cols-2 lg:grid-cols-4">
              <p>姓名：{detail.name}</p><p>等級：{detail.level}</p><p>狀態：{detail.status}</p>
              <p>收藏數：{detail.favoriteCount}｜書評數：{detail.reviewCount}</p>
            </div>
            <p className="font-medium">借閱紀錄（{detail.borrowRecords.length}）</p>
            {detail.borrowRecords.length === 0 ? (
              <div className="rounded-md border border-dashed p-6 text-center text-muted-foreground">尚無借閱紀錄</div>
            ) : (
              <div className="max-h-[50vh] overflow-auto rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>書名</TableHead>
                      <TableHead>借閱日期</TableHead>
                      <TableHead>到期日</TableHead>
                      <TableHead>歸還日</TableHead>
                      <TableHead>狀態</TableHead>
                      <TableHead>逾期／罰款</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {detail.borrowRecords.map((record) => (
                      <TableRow key={record.recordId}>
                        <TableCell className="font-medium">{record.bookTitle}</TableCell>
                        <TableCell>{formatDate(record.borrowDate)}</TableCell>
                        <TableCell>{formatDate(record.dueDate)}</TableCell>
                        <TableCell>{record.returnDate ? formatDate(record.returnDate) : "尚未歸還"}</TableCell>
                        <TableCell><StatusBadge status={record.status} /></TableCell>
                        <TableCell>
                          {record.overdueDays > 0
                            ? `逾期 ${record.overdueDays} 天／NT$${Number(record.fineAmount).toFixed(0)}`
                            : "—"}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </div>}
        </DialogContent>
      </Dialog>
    </>
  )
}
