import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { SearchBar } from "@/components/common/SearchBar";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { EmptyState } from "@/components/common/EmptyState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { Card, CardContent } from "@/components/ui/card";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select";
import { useAsync } from "@/hooks/useAsync";
import { searchBorrowRecords, type BorrowSearchParams } from "@/services/adminService";
import { formatDate } from "@/lib/format";
import { isDueSoon, dueSoonText } from "@/lib/format";

export const Route = createFileRoute("/admin/records")({
  head: () => ({ meta: [{ title: "借閱紀錄管理 — 圖書館借還書系統" }] }),
  component: AdminRecordsPage,
});

function AdminRecordsPage() {
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState<NonNullable<BorrowSearchParams["status"]>>("ALL");

  // searchBorrowRecords
  const { data, loading, error, refetch } = useAsync(
    () => searchBorrowRecords({ keyword, status }).then((r) => r.data),
    [keyword, status],
  );

  return (
    <>
      <PageHeader title="借閱紀錄管理" description="檢視並依學號或書名搜尋所有借閱紀錄。" />
      <Card className="mb-4">
        <CardContent className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center">
          <div className="flex-1">
            <SearchBar placeholder="搜尋學號、姓名或書名..." defaultValue={keyword} onSearch={setKeyword} />
          </div>
          <Select value={status} onValueChange={(v) => setStatus(v as typeof status)}>
            <SelectTrigger className="w-full sm:w-40">
              <SelectValue placeholder="狀態" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">全部狀態</SelectItem>
              <SelectItem value="BORROWED">借出中</SelectItem>
              <SelectItem value="RETURNED">已歸還</SelectItem>
              <SelectItem value="OVERDUE">已逾期</SelectItem>
            </SelectContent>
          </Select>
        </CardContent>
      </Card>
      <Card>
        <CardContent className="p-0">
          {loading ? (
            <LoadingState />
          ) : error ? (
            <ErrorState message={error} onRetry={refetch} />
          ) : !data || data.length === 0 ? (
            <EmptyState title="找不到符合的借閱紀錄" />
          ) : (
            // renderAdminBorrowTable
            (<Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[140px]">學號</TableHead>
                  <TableHead className="w-[120px]">姓名</TableHead>
                  <TableHead>書名</TableHead>
                  <TableHead className="w-[120px]">借書日期</TableHead>
                  <TableHead className="w-[120px]">到期日期</TableHead>
                  <TableHead className="w-[120px]">還書日期</TableHead>
                  <TableHead className="w-[100px]">狀態</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {data.map((r) => (
                    <TableRow
                      key={r.id}
                      className={
                        r.status === "OVERDUE"
                          ? "bg-destructive/5"
                          : r.status !== "RETURNED" && isDueSoon(r.dueDate)
                          ? "bg-yellow-50"
                          : ""
                      }
                    >
                    <TableCell className="font-mono text-xs">{r.studentId}</TableCell>
                    <TableCell>{r.studentName}</TableCell>
                    <TableCell className="font-medium">{r.bookTitle}</TableCell>
                    <TableCell>{formatDate(r.borrowDate)}</TableCell>
                    <TableCell className={r.status === "OVERDUE" ? "font-medium text-destructive" : ""}>
                      <div>
                        <div>{formatDate(r.dueDate)}</div>
                        {r.status !== "RETURNED" && r.status !== "OVERDUE" && isDueSoon(r.dueDate) && (
                          <div className="text-xs text-yellow-700">{dueSoonText(r.dueDate)}</div>
                        )}
                      </div>
                    </TableCell>
                    <TableCell>{formatDate(r.returnDate)}</TableCell>
                    <TableCell><StatusBadge status={r.status} /></TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>)
          )}
        </CardContent>
      </Card>
    </>
  )
}
