import { ApiError, type ApiResponse } from "@/types/api";
import { http } from "./http";

export type PenaltyStatus = "OPEN" | "PAID" | "WAIVED";

export interface PenaltySummary {
  penaltyId: number | null;
  recordId: number;
  bookId: number;
  bookTitle: string;
  borrowDate: string;
  dueDate: string;
  returnDate: string | null;
  overdueDays: number;
  amount: number;
  status: PenaltyStatus | null;
  settled: boolean;
  payable: boolean;
}

export async function getMyPenalties(studentId: string): Promise<ApiResponse<PenaltySummary[]>> {
  try {
    const response = await http.get<PenaltySummary[]>(`/penalties/user/${studentId}`);
    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢罰款失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢罰款失敗");
  }
}

export async function payPenalty(
  penaltyId: number,
  studentId: string,
): Promise<ApiResponse<boolean>> {
  try {
    const response = await http.post<boolean>(`/penalties/${penaltyId}/pay`, { studentId });
    if (!response.success) {
      throw new ApiError(response.message || "繳納罰款失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("繳納罰款失敗");
  }
}
