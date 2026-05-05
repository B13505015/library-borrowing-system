import { ApiError, type ApiResponse } from "@/types/api";
import { http } from "./http";

export async function getMyFavoriteBookIds(userId: number): Promise<ApiResponse<number[]>> {
  try {
    const response = await http.get<number[]>(`/favorites/user/${userId}`);
    if (!response.success || !response.data) throw new ApiError(response.message || "查詢收藏失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢收藏失敗");
  }
}

export async function addFavorite(userId: number, bookId: number): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>("/favorites/add", { userId, bookId });
    if (!response.success) throw new ApiError(response.message || "加入收藏失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("加入收藏失敗");
  }
}

export async function removeFavorite(userId: number, bookId: number): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>("/favorites/remove", { userId, bookId });
    if (!response.success) throw new ApiError(response.message || "移除收藏失敗");
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("移除收藏失敗");
  }
}
