import { ApiError, type ApiResponse } from "@/types/api";
import { http } from "./http";

export interface AdminReview {
  reviewId: number;
  bookId: number;
  bookTitle: string;
  userId: number;
  studentId: string;
  userName: string;
  rating: number;
  content: string;
  createdAt: string;
}

export async function getAdminReviews(keyword = ""): Promise<ApiResponse<AdminReview[]>> {
  try {
    const trimmed = keyword.trim();
    const query = trimmed ? `?keyword=${encodeURIComponent(trimmed)}` : "";
    const response = await http.get<AdminReview[]>(`/admin/reviews${query}`);
    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢書評失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢書評失敗");
  }
}

export async function deleteAdminReview(reviewId: number): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.delete<boolean>(`/admin/reviews/${reviewId}`);
    if (!response.success) {
      throw new ApiError(response.message || "刪除書評失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("刪除書評失敗");
  }
}
