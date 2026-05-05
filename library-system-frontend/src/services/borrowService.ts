import { ApiError, type ApiResponse } from "@/types/api";
import type { BorrowRecord } from "@/types/borrowRecord";
import { http, mockDelay, ok } from "./http";

// 借書：改成真的呼叫後端 API
export async function borrowBook(
  userId: number,
  bookId: number,
  borrowDays = 7
): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>("/borrow", {
      userId,
      bookId,
      borrowDays,
    });

    if (!response.success) {
      throw new ApiError(response.message || "借書失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("借書失敗");
  }
}

// 取得目前使用者借閱紀錄：改成真的呼叫後端 API
export async function getMyBorrowRecords(studentId: string): Promise<ApiResponse<BorrowRecord[]>> {
  try {
    const response = await http.get<BorrowRecord[]>(`/records/user/${studentId}`);

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢借閱紀錄失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("查詢借閱紀錄失敗");
  }
}

// 還書：改成真的呼叫後端 API
export async function handleReturnBook(recordId: string): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>("/return", {
      recordId: Number(recordId),
    });

    if (!response.success) {
      throw new ApiError(response.message || "還書失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("還書失敗");
  }
}