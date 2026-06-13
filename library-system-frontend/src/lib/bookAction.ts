import type { Book } from "@/types/book";

type BookStatus = Book["status"];
type ReservationStatus = "WAITING" | "NOTIFIED" | null | undefined;

export function getBookAction(
  status: BookStatus,
  alreadyBorrowing: boolean,
  reservationStatus?: ReservationStatus,
) {
  if (status === "REMOVED") return { label: "不可借", disabled: true, tone: "disabled" as const };
  if (alreadyBorrowing) return { label: "已借閱", disabled: true, tone: "disabled" as const };
  if (reservationStatus === "NOTIFIED") return { label: "立即借閱", disabled: false, tone: "borrow" as const };
  if (reservationStatus === "WAITING") return { label: "已在隊列中", disabled: true, tone: "disabled" as const };
  if (status === "AVAILABLE") return { label: "借閱", disabled: false, tone: "borrow" as const };
  if (status === "BORROWED") return { label: "預約", disabled: false, tone: "reserve" as const };
  return { label: "不可借", disabled: true, tone: "disabled" as const };
}

export function bookActionClass(tone: "borrow" | "reserve" | "disabled") {
  const size = "h-10 min-w-[112px] justify-center whitespace-nowrap";
  if (tone === "reserve") return `${size} bg-amber-500 text-white hover:bg-amber-600`;
  if (tone === "disabled") return `${size} disabled:cursor-not-allowed disabled:opacity-55`;
  return size;
}
