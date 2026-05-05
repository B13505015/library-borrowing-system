import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

type Status =
  | "AVAILABLE"
  | "BORROWED"
  | "OVERDUE"
  | "RETURNED"
  | "ACTIVE"
  | "SUSPENDED"
  | "DISABLED"
  | "REMOVED";

const LABELS: Record<Status, string> = {
  AVAILABLE: "可借閱",
  BORROWED: "借出中",
  OVERDUE: "已逾期",
  RETURNED: "已歸還",
  ACTIVE: "正常",
  SUSPENDED: "已停權",
  DISABLED: "已停用",
  REMOVED: "已下架",
};

const CLASSES: Record<Status, string> = {
  AVAILABLE: "bg-success/15 text-success border-success/30 hover:bg-success/20",
  BORROWED: "bg-info/15 text-info border-info/30 hover:bg-info/20",
  OVERDUE: "bg-destructive/15 text-destructive border-destructive/30 hover:bg-destructive/20",
  RETURNED: "bg-muted text-muted-foreground border-border hover:bg-muted",
  ACTIVE: "bg-success/15 text-success border-success/30 hover:bg-success/20",
  SUSPENDED: "bg-destructive/15 text-destructive border-destructive/30 hover:bg-destructive/20",
  DISABLED: "bg-muted text-muted-foreground border-border hover:bg-muted",
  REMOVED: "bg-muted text-muted-foreground border-border hover:bg-muted",
};

export function StatusBadge({ status, className }: { status: Status; className?: string }) {
  return (
    <Badge variant="outline" className={cn("font-medium", CLASSES[status], className)}>
      {LABELS[status]}
    </Badge>
  );
}