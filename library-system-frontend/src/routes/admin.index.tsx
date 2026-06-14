import { createFileRoute } from "@tanstack/react-router";
import { useMemo } from "react";
import { AlertTriangle, BookCopy, BookOpen, Users } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { PageHeader } from "@/components/layout/PageHeader";
import { StatCard } from "@/components/common/StatCard";
import { StatusBadge } from "@/components/common/StatusBadge";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { useAsync } from "@/hooks/useAsync";
import { fetchDashboardStats, getRecentBorrowRecords, getOverdueRecords } from "@/services/adminService";
import { useAuth } from "@/context/AuthContext";
import { formatDate } from "@/lib/format";

export const Route = createFileRoute("/admin/")({
  head: () => ({ meta: [{ title: "管理員總覽 — 圖書館借還書系統" }] }),
  component: AdminDashboardPage,
});

function AdminDashboardPage() {
  const { user } = useAuth();
  const stats = useAsync(() => fetchDashboardStats().then((r) => r.data), []);
  const recent = useAsync(() => getRecentBorrowRecords(6).then((r) => r.data), []);
  const overdue = useAsync(() => getOverdueRecords().then((r) => r.data), []);

  const uniqueOverdue = useMemo(() => {
    const rows = overdue.data ?? [];
    const map = new Map<string, (typeof rows)[number]>();
    for (const row of rows) {
      const dedupeKey = `${row.studentId}::${row.bookTitle}`;
      const existing = map.get(dedupeKey);
      if (!existing || new Date(row.dueDate).getTime() < new Date(existing.dueDate).getTime()) {
        map.set(dedupeKey, row);
      }
    }
    return Array.from(map.values());
  }, [overdue.data]);

  return (
    <>
      <PageHeader
        title="管理員總覽"
        description={`${user?.name ?? "管理員"}　|　最後更新：${formatDate(new Date().toISOString())}`}
      />

      {stats.loading ? (
        <LoadingState />
      ) : stats.error ? (
        <ErrorState message={stats.error} onRetry={stats.refetch} />
      ) : (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <StatCard label="總書籍數" value={stats.data?.totalBooks ?? 0} icon={BookCopy} tone="info" />
          <StatCard label="借出中" value={stats.data?.borrowedCount ?? 0} icon={BookOpen} tone="default" />
          <StatCard label="使用者數量" value={stats.data?.totalUsers ?? 0} icon={Users} tone="success" />
          <StatCard label="逾期未還" value={stats.data?.overdueCount ?? 0} icon={AlertTriangle} tone="destructive" />
        </div>
      )}

      <div className="mt-6 grid gap-5 lg:grid-cols-3">
        <Card className="border-0 bg-card/80 shadow-sm lg:col-span-2">
          <CardContent className="p-6">
            <h2 className="mb-4 text-lg font-semibold">最近借閱活動</h2>
            {recent.loading ? (
              <LoadingState />
            ) : recent.error ? (
              <ErrorState message={recent.error} onRetry={recent.refetch} />
            ) : !recent.data || recent.data.length === 0 ? (
              <p className="rounded-lg border border-dashed py-8 text-center text-sm text-muted-foreground">暫無資料</p>
            ) : (
              <ul className="divide-y divide-border">
                {recent.data.map((r) => (
                  <li key={r.id} className="flex items-center justify-between py-3">
                    <div>
                      <p className="font-medium">{r.bookTitle}</p>
                      <p className="text-xs text-muted-foreground">
                        {r.studentName}（{r.studentId}）　借閱：{formatDate(r.borrowDate)}
                      </p>
                    </div>
                    <StatusBadge status={r.status} />
                  </li>
                ))}
              </ul>
            )}
          </CardContent>
        </Card>

        <Card className="self-start border-0 bg-card/80 shadow-sm">
          <CardContent className="p-6">
            <div className="mb-4 flex items-center gap-2">
              <AlertTriangle className="h-5 w-5 text-destructive" />
              <h2 className="text-lg font-semibold">逾期提醒（{uniqueOverdue.length}）</h2>
            </div>
            {overdue.loading ? (
              <LoadingState />
            ) : overdue.error ? (
              <ErrorState message={overdue.error} onRetry={overdue.refetch} />
            ) : uniqueOverdue.length === 0 ? (
              <p className="rounded-lg border border-dashed p-4 text-sm text-muted-foreground">目前無逾期紀錄。</p>
            ) : (
              <ul className="max-h-[280px] space-y-1.5 overflow-y-auto pr-1">
                {uniqueOverdue.map((r) => (
                  <li key={r.id} className="rounded-lg border border-destructive/30 bg-destructive/5 px-3 py-2">
                    <p className="text-sm font-medium leading-tight text-destructive">{r.bookTitle}</p>
                    <p className="mt-0.5 text-xs leading-tight text-destructive/80">
                      {r.studentName}（{r.studentId}）｜到期 {formatDate(r.dueDate)}
                    </p>
                  </li>
                ))}
              </ul>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  );
}
