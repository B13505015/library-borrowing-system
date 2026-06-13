import { ApiError, type ApiResponse } from "@/types/api";
import type { Book, BookFormValues } from "@/types/book";
import { http } from "./http";
import type { BorrowRecord } from "@/types/borrowRecord";


function normalizeBookStatus(status: string): Book["status"] {
  const normalized = status.toUpperCase();
  if (normalized === "AVAILABLE" || normalized === "BORROWED" || normalized === "REMOVED") {
    return normalized;
  }
  return "BORROWED";
}

function normalizeBooks(books: Book[]): Book[] {
  return books.map((book) => ({ ...book, status: normalizeBookStatus(String(book.status ?? "")) }));
}

export type PopularBook = {
  bookId: number;
  title: string;
  borrowCount: number;
  avgRating: number;
  reviewCount: number;
};

// 查全部書籍
export async function getAllBooks(): Promise<ApiResponse<Book[]>> {
  try {
    const response = await http.get<Book[]>("/books");

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢書籍失敗");
    }

    return { ...response, data: normalizeBooks(response.data) };
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("查詢書籍失敗");
  }
}

// 關鍵字搜尋書籍
export async function searchBooks(keyword = ""): Promise<ApiResponse<Book[]>> {
  try {
    const trimmed = keyword.trim();

    if (!trimmed) {
      return await getAllBooks();
    }

    const response = await http.get<Book[]>(
      `/books/search?keyword=${encodeURIComponent(trimmed)}`
    );

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "搜尋書籍失敗");
    }

    return { ...response, data: normalizeBooks(response.data) };
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("搜尋書籍失敗");
  }
}

// 管理端新增 / 編輯 / 下架書籍：已接真後端 API
export async function handleAddBook(values: BookFormValues): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>("/books", {
      title: values.title,
      publisher: values.publisher,
      publishYear: values.publishYear,
      edition: values.edition,
      format: values.format,
      source: values.source,
      note: values.note,
    });

    if (!response.success) {
      throw new ApiError(response.message || "新增書籍失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("新增書籍失敗");
  }
}

export async function handleEditBook(id: string, values: BookFormValues): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.put<boolean>(`/books/${id}`, {
      title: values.title,
      publisher: values.publisher,
      publishYear: values.publishYear,
      edition: values.edition,
      format: values.format,
      source: values.source,
      note: values.note,
    });

    if (!response.success) {
      throw new ApiError(response.message || "編輯書籍失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("編輯書籍失敗");
  }
}

export async function handleRemoveBook(id: string): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.delete<boolean>(`/books/${id}`);

    if (!response.success) {
      throw new ApiError(response.message || "下架書籍失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("下架書籍失敗");
  }
}

export async function getBookBorrowHistory(bookId: string | number): Promise<ApiResponse<BorrowRecord[]>> {
  try {
    const response = await http.get<BorrowRecord[]>(`/books/${bookId}/history`);

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢書籍借還紀錄失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("查詢書籍借還紀錄失敗");
  }
}

export async function getPopularBooks(sortBy: "borrow" | "rating", limit = 5): Promise<ApiResponse<PopularBook[]>> {
  try {
    const response = await http.get<PopularBook[]>(`/books/popular?sortBy=${sortBy}&limit=${limit}`);
    if (!response.success || !response.data) throw new ApiError(response.message || "查詢熱門書籍失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢熱門書籍失敗");
  }
}



export type ReservationInfo = {
  waitingCount: number;
  myQueuePosition: number | null;
  alreadyBorrowing: boolean;
  alreadyReserved: boolean;
  activeReservationStatus: "WAITING" | "NOTIFIED" | null;
  reservationId: number | null;
};

export type MyReservation = {
  reservationId: number;
  bookId: number;
  title: string;
  status: "WAITING" | "NOTIFIED";
  queuePosition: number | null;
  queuePriority: number;
  createdAt: string | null;
  notifiedAt: string | null;
  expiresAt: string | null;
};

export async function getReservationInfo(bookId: string | number, userId?: number): Promise<ApiResponse<ReservationInfo>> {
  try {
    const query = userId ? `?userId=${userId}` : "";
    const response = await http.get<ReservationInfo>(`/books/${bookId}/reservation-info${query}`);
    if (!response.success || !response.data) throw new ApiError(response.message || "查詢預約資訊失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢預約資訊失敗");
  }
}


export async function getReservationNotifications(userId: number): Promise<ApiResponse<string[]>> {
  try {
    const response = await http.get<string[]>(`/books/reservation-notifications?userId=${userId}`);
    if (!response.success || !response.data) throw new ApiError(response.message || "查詢預約通知失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢預約通知失敗");
  }
}


export async function getMyReservations(userId: number): Promise<ApiResponse<MyReservation[]>> {
  try {
    const response = await http.get<MyReservation[]>(`/books/my-reservations?userId=${userId}`);
    if (!response.success || !response.data) throw new ApiError(response.message || "查詢我的預約失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢我的預約失敗");
  }
}
