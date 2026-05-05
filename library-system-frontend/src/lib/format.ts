export function formatDate(iso?: string | null): string {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "—";
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${yyyy}/${mm}/${dd}`;
}

export function isOverdue(dueIso: string, returnIso?: string | null): boolean {
  if (returnIso) return false;
  return new Date(dueIso).getTime() < Date.now();
}

export function daysUntil(iso: string): number {
  const ms = new Date(iso).getTime() - Date.now();
  return Math.ceil(ms / (1000 * 60 * 60 * 24));
}

export const DUE_SOON_DAYS = 3;

export function isDueSoon(date: string | null | undefined, threshold = DUE_SOON_DAYS): boolean {
  if (!date) return false;

  const diffMs = new Date(date).getTime() - Date.now();
  const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

  return diffDays >= 0 && diffDays <= threshold;
}

export function dueSoonText(date: string | null | undefined, threshold = DUE_SOON_DAYS): string {
  if (!date) return "";

  const diffMs = new Date(date).getTime() - Date.now();
  const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

  if (diffDays < 0) return "";
  if (diffDays === 0) return "今天到期";
  if (diffDays === 1) return "明天到期";
  if (diffDays <= threshold) return `剩 ${diffDays} 天到期`;

  return "";
}