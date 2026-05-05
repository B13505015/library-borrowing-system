import { createFileRoute, Link } from "@tanstack/react-router";
import { BookOpen, Clock, ClipboardList, AlertTriangle } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { PageHeader } from "@/components/layout/PageHeader";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { EmptyState } from "@/components/common/EmptyState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { useAsync } from "@/hooks/useAsync";
import { useAuth } from "@/context/AuthContext";
import { getMyBorrowRecords } from "@/services/borrowService";
import { formatDate, daysUntil } from "@/lib/format";
import { isDueSoon, dueSoonText } from "@/lib/format";

export const Route = createFileRoute("/user/")({
  head: () => ({ meta: [{ title: "首頁 — 圖書館借還書系統" }] }),
  component: UserDashboardPage,
});

function UserDashboardPage() {
  const { user } = useAuth();
  const { data, loading, error, refetch } = useAsync(
    () => getMyBorrowRecords(user!.studentId).then((r) => r.data),
    [user?.studentId],
  );

  const active = (data ?? []).filter((r) => r.status !== "RETURNED");
  const overdue = (data ?? []).filter((r) => r.status === "OVERDUE");
  const dueSoon = active.filter((r) => r.status !== "OVERDUE" && isDueSoon(r.dueDate));

  return (
    <>
      <PageHeader
        title={`歡迎回來，${user?.name ?? ""}`}
        description={`學號 ${user?.studentId}　|　身分 ${user?.level === "VIP" ? "VIP 使用者" : "一般使用者"}　|　願您閱讀愉快`}
      />

      {loading ? (
        <LoadingState />
      ) : error ? (
        <ErrorState message={error} onRetry={refetch} />
      ) : (
        <div className="grid gap-6 lg:grid-cols-3">
          <Card className="lg:col-span-2">
            <CardContent className="p-6">
              <div className="mb-4 flex items-center justify-between">
                <h2 className="text-lg font-semibold">借閱中的書籍</h2>
                <Link to="/user/records" className="text-sm text-primary hover:underline">
                  查看全部 →
                </Link>
              </div>
              {/* renderRecentBorrowRecords */}
              {active.length === 0 ? (
                <EmptyState title="目前沒有借閱中的書籍" description="前往書籍查詢開始借閱您感興趣的書。" />
              ) : (
                <ul className="divide-y divide-border">
                  {active.slice(0, 5).map((r) => {
                    const days = daysUntil(r.dueDate);
                    return (
                      <li key={r.id} className="flex items-center justify-between gap-3 py-3">
                        <div>
                          <p className="font-medium">{r.bookTitle}</p>
                          <p className="text-xs text-muted-foreground">
                            借閱：{formatDate(r.borrowDate)}　到期：{formatDate(r.dueDate)}
                            {r.status !== "OVERDUE" && days >= 0 && `（剩 ${days} 天）`}
                          </p>
                        </div>
                        <StatusBadge status={r.status} />
                      </li>
                    );
                  })}
                </ul>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="mb-4 flex items-center gap-2">
                <AlertTriangle className="h-5 w-5 text-destructive" />
                <h2 className="text-lg font-semibold">逾期提醒</h2>
              </div>
              {/* renderOverdueRecords */}
              {overdue.length === 0 ? (
                <p className="text-sm text-muted-foreground">目前沒有逾期書籍，請繼續保持。</p>
              ) : (
                <ul className="space-y-3">
                  {overdue.map((r) => (
                    <li key={r.id} className="rounded-md border border-destructive/30 bg-destructive/5 p-3">
                      <p className="font-medium text-destructive">{r.bookTitle}</p>
                      <p className="text-xs text-destructive/80">
                        到期日：{formatDate(r.dueDate)}（已逾期 {Math.abs(daysUntil(r.dueDate))} 天）
                      </p>
                    </li>
                  ))}
                </ul>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="mb-4 flex items-center gap-2">
                <AlertTriangle className="h-5 w-5 text-warning" />
                <h2 className="text-lg font-semibold">即將到期提醒</h2>
              </div>

              {dueSoon.length === 0 ? (
                <p className="text-sm text-muted-foreground">目前沒有即將到期的書籍。</p>
              ) : (
                <ul className="space-y-3">
                  {dueSoon.map((r) => (
                    <li key={r.id} className="rounded-md border border-yellow-300 bg-yellow-50 p-3">
                      <p className="font-medium">{r.bookTitle}</p>
                      <p className="text-xs text-muted-foreground">
                        到期日：{formatDate(r.dueDate)}（{dueSoonText(r.dueDate)}）
                      </p>
                    </li>
                  ))}
                </ul>
              )}
            </CardContent>
          </Card>

          <QuickAction to="/user/books" icon={BookOpen} title="查詢書籍" desc="搜尋館藏並借閱" />
          <QuickAction to="/user/records" icon={ClipboardList} title="我的借閱紀錄" desc="查看與歸還" />
          <QuickAction to="/user" icon={Clock} title="館藏概況" desc={`借閱中 ${active.length} 本`} />
        </div>
      )}
    </>
  );
}

function QuickAction({
  to,
  icon: Icon,
  title,
  desc,
}: {
  to: "/user" | "/user/books" | "/user/records";
  icon: typeof BookOpen;
  title: string;
  desc: string;
}) {
  return (
    <Card className="transition-shadow hover:shadow-md">
      <CardContent className="flex items-center gap-4 p-6">
        <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10 text-primary">
          <Icon className="h-6 w-6" />
        </div>
        <div className="flex-1">
          <p className="font-medium">{title}</p>
          <p className="text-xs text-muted-foreground">{desc}</p>
        </div>
        <Button asChild variant="ghost" size="sm">
          <Link to={to}>前往</Link>
        </Button>
      </CardContent>
    </Card>
  );
}
