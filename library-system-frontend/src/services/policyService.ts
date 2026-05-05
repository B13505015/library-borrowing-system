import { ApiError, type ApiResponse } from "@/types/api";
import { http } from "./http";

export interface LoanPolicy {
  roleLevel: "NORMAL" | "VIP";
  maxActiveLoans: number;
  overdueFinePerDay: number;
  reservationPriority: number;
  fineGraceDays: number;
}

export async function getLoanPolicy(roleLevel: "NORMAL" | "VIP"): Promise<ApiResponse<LoanPolicy>> {
  try {
    const response = await http.get<LoanPolicy>(`/admin/policies/loan?roleLevel=${roleLevel}`);
    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢借閱政策失敗");
    }
    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢借閱政策失敗");
  }
}
