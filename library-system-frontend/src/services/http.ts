/**
 * Future HTTP layer.
 * When connecting to the Java backend, implement get/post/put/delete here
 * (e.g. using fetch / axios), and replace mock implementations inside
 * the service files with calls to these helpers.
 */
import type { ApiResponse } from "@/types/api";
import { ApiError } from "@/types/api";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api";// TODO: replace with Java backend base URL

export function getAuthToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem("auth_token");
}

export function setAuthToken(token: string | null) {
  if (typeof window === "undefined") return;
  if (token) window.localStorage.setItem("auth_token", token);
  else window.localStorage.removeItem("auth_token");
}

// Simulate latency for mock services
export const mockDelay = (ms = 300) => new Promise((r) => setTimeout(r, ms));

// Generic helpers (placeholder — for future real API)
async function request<T>(path: string, init: RequestInit = {}): Promise<ApiResponse<T>> {
  const token = getAuthToken();
  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init.headers ?? {}),
    },
  });
  if (!res.ok) throw new ApiError(`Request failed: ${res.status}`, res.status);
  return (await res.json()) as ApiResponse<T>;
}

export const http = {
  get: <T>(path: string) => request<T>(path, { method: "GET" }),
  post: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "POST", body: JSON.stringify(body ?? {}) }),
  put: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "PUT", body: JSON.stringify(body ?? {}) }),
  patch: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: "PATCH", body: JSON.stringify(body ?? {}) }),
  delete: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};

export function ok<T>(data: T, message?: string): ApiResponse<T> {
  return { success: true, data, message };
}
