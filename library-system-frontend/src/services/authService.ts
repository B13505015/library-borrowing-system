import { ApiError, type ApiResponse } from "@/types/api";
import type { AuthSession, LoginRequest, RegisterRequest } from "@/types/auth";
import { http, mockDelay, ok, setAuthToken } from "./http";

/**
 * Authentication Service.
 * 現在先把使用者登入改成真的呼叫 Java 後端 API。
 * 管理員登入與註冊改成真的呼叫 Java 後端 API。
 */

// 使用者登入：改成真的呼叫後端 API
export async function handleUserLogin(req: LoginRequest): Promise<ApiResponse<AuthSession>> {
  try {
    const response = await http.post<AuthSession>("/auth/user-login", {
      studentId: req.studentId,
      password: req.password,
    });

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "使用者登入失敗");
    }

    if (response.data.token) {
      setAuthToken(response.data.token);
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("使用者登入失敗");
  }
}

// 管理員登入：改成真的呼叫後端 API
export async function handleAdminLogin(req: LoginRequest): Promise<ApiResponse<AuthSession>> {
  try {
    const response = await http.post<AuthSession>("/auth/admin-login", {
      username: req.studentId,
      password: req.password,
    });

    if (!response.success || !response.data) {
      throw new ApiError(response.message || "管理員登入失敗");
    }

    if (response.data.token) {
      setAuthToken(response.data.token);
    }

    return response;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("管理員登入失敗");
  }
}

export async function handleRegister(req: RegisterRequest): Promise<ApiResponse<{ studentId: string }>> {
  try {
    const response = await http.post<boolean>("/auth/register", {
      studentId: req.studentId,
      name: req.name,
      password: req.password,
      level: req.level,
    });

    if (!response.success) {
      throw new ApiError(response.message || "註冊失敗");
    }

    return {
      success: true,
      data: { studentId: req.studentId },
      message: response.message || "註冊成功，請使用您的學號登入",
    };
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError("註冊失敗");
  }
}

export async function handleLogout() {
  setAuthToken(null);
  return ok(true);
}