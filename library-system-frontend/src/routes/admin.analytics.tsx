import { createFileRoute } from "@tanstack/react-router";
import { BarChart3 } from "lucide-react";
import { EmptyState } from "@/components/common/EmptyState";
import { ErrorState } from "@/components/common/ErrorState";
import { LoadingState } from "@/components/common/LoadingState";
import { PageHeader } from "@/components/layout/PageHeader";
import { Card, CardContent } from "@/components/ui/card";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { useAsync } from "@/hooks/useAsync";
import { getSubjectBorrowStats } from "@/services/adminService";

export const Route = createFileRoute("/admin/analytics")({
  head: () => ({ meta: [{ title: "書籍分析 — 圖書館借還書系統" }] }),
  component: AdminAnalyticsPage,
});

function AdminAnalyticsPage() {
  const subjectStats = useAsync(() => getSubjectBorrowStats().then((response) => response.data), []);
  const topSubjects = (subjectStats.data ?? []).slice(0, 5);
  const highestBorrowCount = topSubjects[0]?.borrowCount ?? 0;

  return (
    <>
      <PageHeader title="書籍分析" description="依借閱紀錄統計各主題的熱門程度。" />
      <Card className="border-0 bg-card/80 shadow-sm">
        <CardContent className="p-6">
          <div className="mb-5 flex items-center gap-3">
            <div className="rounded-lg bg-primary/10 p-2 text-primary">
              <BarChart3 className="h-5 w-5" />
            </div>
            <div>
              <h2 className="text-lg font-semibold">主題借閱分析</h2>
              <p className="text-sm text-muted-foreground">依借閱紀錄統計各主題的熱門程度。</p>
            </div>
          </div>

          {subjectStats.loading ? (
            <LoadingState />
          ) : subjectStats.error ? (
            <ErrorState message={subjectStats.error} onRetry={subjectStats.refetch} />
          ) : !subjectStats.data || subjectStats.data.length === 0 ? (
            <EmptyState title="目前尚無主題借閱資料" />
          ) : (
            <div className="grid gap-8 xl:grid-cols-[minmax(0,1fr)_minmax(440px,1.2fr)]">
              <section>
                <h3 className="mb-4 text-sm font-semibold">熱門主題 Top 5</h3>
                <div className="space-y-4">
                  {topSubjects.map((item, index) => {
                    const width = highestBorrowCount > 0
                      ? (item.borrowCount / highestBorrowCount) * 100
                      : 0;
                    return (
                      <div key={item.subject}>
                        <div className="mb-1.5 flex items-center justify-between gap-3 text-sm">
                          <span className="min-w-0 truncate font-medium">
                            {index + 1}. {item.subject}
                          </span>
                          <span className="shrink-0 text-muted-foreground">{item.borrowCount} 次</span>
                        </div>
                        <div className="h-2.5 overflow-hidden rounded-full bg-muted">
                          <div
                            className="h-full rounded-full bg-primary transition-[width]"
                            style={{ width: `${width}%` }}
                          />
                        </div>
                      </div>
                    );
                  })}
                </div>
              </section>

              <section>
                <h3 className="mb-2 text-sm font-semibold">主題借閱排行</h3>
                <div className="overflow-hidden rounded-lg border">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead className="w-[70px]">排名</TableHead>
                        <TableHead>主題</TableHead>
                        <TableHead className="w-[110px] text-right">借閱次數</TableHead>
                        <TableHead className="w-[100px] text-right">佔比</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {subjectStats.data.map((item, index) => (
                        <TableRow key={item.subject}>
                          <TableCell className="font-medium">{index + 1}</TableCell>
                          <TableCell>{item.subject}</TableCell>
                          <TableCell className="text-right">{item.borrowCount}</TableCell>
                          <TableCell className="text-right">{item.percentage.toFixed(2)}%</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              </section>
            </div>
          )}
        </CardContent>
      </Card>
    </>
  );
}
