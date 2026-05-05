import { ApiError, type ApiResponse } from "@/types/api";
import type { AppUser } from "@/types/user";
import type { BorrowRecord } from "@/types/borrowRecord";
import { http, mockDelay, ok } from "./http";

export interface DashboardStats {
  totalBooks: number;
  borrowedCount: number;
  totalUsers: number;
  overdueCount: number;
}

export async function fetchDashboardStats(): Promise<ApiResponse<DashboardStats>> {
  try {
    const response = await http.get<DashboardStats>("/admin/dashboard/stats");

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢總覽統計失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢總覽統計失敗");
  }
}

export async function getRecentBorrowRecords(limit = 5): Promise<ApiResponse<BorrowRecord[]>> {
  try {
    const response = await http.get<BorrowRecord[]>(
      `/admin/dashboard/recent-borrows?limit=${limit}`
    );

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢最近借閱失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢最近借閱失敗");
  }
}

export async function getOverdueRecords(): Promise<ApiResponse<BorrowRecord[]>> {
  try {
    const response = await http.get<BorrowRecord[]>(
      "/admin/dashboard/overdue-records"
    );

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢逾期紀錄失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError("查詢逾期紀錄失敗");
  }
}

export async function searchUsers(keyword = ""): Promise<ApiResponse<AppUser[]>> {
  try {
    const trimmed = keyword.trim();
    const path = trimmed
      ? `/admin/users?keyword=${encodeURIComponent(trimmed)}`
      : "/admin/users";

    const response = await http.get<AppUser[]>(path);

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "查詢使用者失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("查詢使用者失敗");
  }
}

export async function handleSuspendUser(studentId: string): Promise<ApiResponse<AppUser>> {
  try {
    const response = await http.patch<AppUser>(`/admin/users/${studentId}/suspend`);

    if (!response.success) {
      throw new ApiError(response.message || "停權失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("停權失敗");
  }
}

export async function handleActivateUser(studentId: string): Promise<ApiResponse<AppUser>> {
  try {
    const response = await http.patch<AppUser>(`/admin/users/${studentId}/activate`);

    if (!response.success) {
      throw new ApiError(response.message || "復權失敗");
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("復權失敗");
  }
}

export interface BorrowSearchParams {
  keyword?: string;
  status?: BorrowRecord["status"] | "ALL";
}

export async function searchBorrowRecords(
  params: BorrowSearchParams = {}
): Promise<ApiResponse<BorrowRecord[]>> {
  try {
    const searchParams = new URLSearchParams();

    if (params.keyword?.trim()) {
      searchParams.set("keyword", params.keyword.trim());
    }

    if (params.status && params.status !== "ALL") {
      searchParams.set("status", params.status);
    } else {
      searchParams.set("status", "ALL");
    }

    const response = await http.get<BorrowRecord[]>(
      `/admin/records?${searchParams.toString()}`
    );

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