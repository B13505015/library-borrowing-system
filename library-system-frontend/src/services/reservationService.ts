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

export async function cancelReservation(userId: number, reservationId: number): Promise<ApiResponse<boolean>> {
  const response = await http.put<boolean>(`/reservations/${reservationId}/cancel`, { userId });
  if (!response.success) throw new ApiError(response.message || "取消預約失敗");
  return response;
}

export async function fulfillReservation(
  userId: number,
  reservationId: number,
  borrowDays: number,
): Promise<ApiResponse<boolean>> {
  const response = await http.post<boolean>(`/reservations/${reservationId}/fulfill`, {
    userId,
    borrowDays,
  });
  if (!response.success) throw new ApiError(response.message || "借閱失敗");
  return response;
}
