import { ApiError, type ApiResponse } from "@/types/api";
import { http } from "./http";

export async function getBookReviews(bookId: number): Promise<ApiResponse<string[]>> {
  try {
    const response = await http.get<string[]>(`/reviews/book/${bookId}`);
    if (!response.success || !response.data) throw new ApiError(response.message || "查詢書評失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢書評失敗");
  }
}

export async function addReview(userId: number, bookId: number, rating: number, comment: string): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>("/reviews", { userId, bookId, rating, comment });
    if (!response.success) throw new ApiError(response.message || "新增書評失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("新增書評失敗");
  }
}
