export type BookStatus = "AVAILABLE" | "BORROWED" | "REMOVED";

export interface Book {
  id: string;
  title: string;
  publisher: string;
  publishYear: number;
  edition: string;
  format: string;
  source: string;
  note: string;
  status: BookStatus;
}

export type BookFormValues = Omit<Book, "id" | "status"> & { status?: BookStatus };
