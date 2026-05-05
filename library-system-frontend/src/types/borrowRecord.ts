export type BorrowStatus = "BORROWED" | "RETURNED" | "OVERDUE";

export interface BorrowRecord {
  id: string;
  studentId: string;
  studentName: string;
  bookId: string;
  bookTitle: string;
  borrowDate: string; // ISO
  dueDate: string;
  returnDate?: string | null;
  status: BorrowStatus;
}
