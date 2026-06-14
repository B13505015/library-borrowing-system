import { ApiError, type ApiResponse } from "@/types/api";
import { http } from "./http";

export type AdminPenaltyStatus = "OPEN" | "PAID" | "WAIVED" | "ACCRUING";
export type AdminPenaltyFilter = "ALL" | "ACCRUING" | "UNPAID" | "PAID" | "WAIVED";

export interface AdminPenalty {
  penaltyId: number | null;
  recordId: number;
  userId: number;
  studentId: string;
  userName: string;
  bookId: number;
  bookTitle: string;
  borrowDate: string;
  dueDate: string;
  returnDate: string | null;
  overdueDays: number;
  amount: number;
  status: AdminPenaltyStatus;
  settled: boolean;
  payable: boolean;
}

export async function getAdminPenalties(
  keyword = "",
  status: AdminPenaltyFilter = "ALL",
): Promise<ApiResponse<AdminPenalty[]>> {
  try {
    const params = new URLSearchParams();
    if (keyword.trim()) params.set("keyword", keyword.trim());
    if (status !== "ALL") params.set("status", status);
    const query = params.toString() ? `?${params.toString()}` : "";
    const response = await http.get<AdminPenalty[]>(`/admin/penalties${query}`);
    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢罰款失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢罰款失敗");
  }
}

export async function updatePenaltyStatus(
  penaltyId: number,
  status: "PAID" | "WAIVED",
): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.put<boolean>(`/admin/penalties/${penaltyId}/status`, { status });
    if (!response.success) {
      throw new ApiError(response.message || "更新罰款狀態失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("更新罰款狀態失敗");
  }
}
