import { Inbox } from "lucide-react";
import type { ReactNode } from "react";

export function EmptyState({ title = "暫無資料", description, action }: { title?: string; description?: string; action?: ReactNode }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
      <div className="flex h-14 w-14 items-center justify-center rounded-full bg-muted text-muted-foreground">
        <Inbox className="h-7 w-7" />
      </div>
      <p className="text-base font-medium">{title}</p>
      {description && <p className="max-w-sm text-sm text-muted-foreground">{description}</p>}
      {action}
    </div>
  );
}
