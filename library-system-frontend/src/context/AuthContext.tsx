import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import type { AppUser } from "@/types/user";
import type { LoginRequest, RegisterRequest } from "@/types/auth";
import {
  handleAdminLogin as svcAdminLogin,
  handleLogout as svcLogout,
  handleRegister as svcRegister,
  handleUserLogin as svcUserLogin,
} from "@/services/authService";

interface AuthContextValue {
  user: AppUser | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  loginAsUser: (req: LoginRequest) => Promise<AppUser>;
  loginAsAdmin: (req: LoginRequest) => Promise<AppUser>;
  register: (req: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);
const STORAGE_KEY = "auth_user";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AppUser | null>(null);

  useEffect(() => {
    if (typeof window === "undefined") return;
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (raw) {
      try {
        setUser(JSON.parse(raw));
      } catch {
        // ignore
      }
    }
  }, []);

  const persist = (u: AppUser | null) => {
    setUser(u);
    if (typeof window !== "undefined") {
      if (u) window.localStorage.setItem(STORAGE_KEY, JSON.stringify(u));
      else window.localStorage.removeItem(STORAGE_KEY);
    }
  };

const loginAsUser = useCallback(async (req: LoginRequest) => {
  const res = await svcUserLogin(req);

  if (!res.success || !res.data) {
    throw new Error(res.message || "使用者登入失敗");
  }

  persist(res.data.user);
  return res.data.user;
}, []);

const loginAsAdmin = useCallback(async (req: LoginRequest) => {
  const res = await svcAdminLogin(req);

  if (!res.success || !res.data) {
    throw new Error(res.message || "管理員登入失敗");
  }

  persist(res.data.user);
  return res.data.user;
}, []);

  const register = useCallback(async (req: RegisterRequest) => {
    await svcRegister(req);
  }, []);

  const logout = useCallback(async () => {
    await svcLogout();
    persist(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: !!user,
      isAdmin: user?.role === "ADMIN",
      loginAsUser,
      loginAsAdmin,
      register,
      logout,
    }),
    [user, loginAsUser, loginAsAdmin, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>");
  return ctx;
}
