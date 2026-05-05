import { AlertTriangle } from "lucide-react";
import { Button } from "@/components/ui/button";

export function ErrorState({ message = "資料載入失敗", onRetry }: { message?: string; onRetry?: () => void }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
      <div className="flex h-14 w-14 items-center justify-center rounded-full bg-destructive/10 text-destructive">
        <AlertTriangle className="h-7 w-7" />
      </div>
      <p className="text-base font-medium">{message}</p>
      {onRetry && (
        <Button variant="outline" onClick={onRetry}>
          重新載入
        </Button>
      )}
    </div>
  );
}
