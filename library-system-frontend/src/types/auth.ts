import type { AppUser } from "./user";

export interface LoginRequest {
  studentId: string;
  password: string;
}

export interface RegisterRequest {
  studentId: string;
  name: string;
  password: string;
  level: "NORMAL" | "VIP";
}

export interface AuthSession {
  token: string;
  user: AppUser;
}
