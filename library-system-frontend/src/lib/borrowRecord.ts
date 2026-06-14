import type { BorrowRecord } from "@/types/borrowRecord";

export function isActiveBorrowRecord(record: BorrowRecord): boolean {
  return !record.returnDate;
}

type BookReference = {
  id?: string | number;
  bookId?: string | number;
  title: string;
};

export function getActiveBorrowedBookIds(
  records: BorrowRecord[],
  books: BookReference[] = [],
): Set<number> {
  const bookIdByTitle = new Map(
    books.map((book) => [book.title, Number(book.bookId ?? book.id)]),
  );

  return new Set(
    records
      .filter(isActiveBorrowRecord)
      .map((record) => Number(record.bookId) || bookIdByTitle.get(record.bookTitle))
      .filter((bookId): bookId is number => typeof bookId === "number" && Number.isFinite(bookId)),
  );
}
