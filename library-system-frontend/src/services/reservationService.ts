import { ApiError, type ApiResponse } from "@/types/api";
import { http } from "./http";

export async function reserveBook(userId: number, bookId: number): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>("/reservations", { userId, bookId });
    if (!response.success) {
      throw new ApiError(response.message || "預約失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("預約失敗");
  }
}
