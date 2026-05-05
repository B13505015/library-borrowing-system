import { Loader2 } from "lucide-react";

export function LoadingState({ label = "載入中..." }: { label?: string }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-muted-foreground">
      <Loader2 className="h-8 w-8 animate-spin" />
      <p className="text-sm">{label}</p>
    </div>
  );
}
