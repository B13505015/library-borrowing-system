import { createFileRoute, Outlet, redirect, useNavigate } from "@tanstack/react-router";
import { Sidebar } from "@/components/layout/Sidebar";
import { Button } from "@/components/ui/button";
import { LogOut } from "lucide-react";
import { useAuth } from "@/context/AuthContext";

function getUserFromStorage() {
  if (typeof window === "undefined") return null;
  try {
    const raw = window.localStorage.getItem("auth_user");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export const Route = createFileRoute("/admin")({
  beforeLoad: () => {
    const u = getUserFromStorage();
    if (!u) throw redirect({ to: "/login" });
    if (u.role !== "ADMIN") throw redirect({ to: "/user" });
  },
  component: AdminLayout,
});

function AdminLayout() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate({ to: "/login" });
  };

  return (
    <div className="flex min-h-screen w-full bg-muted/30">
      <Sidebar />
      <main className="relative flex-1 overflow-x-hidden px-8 py-8">
        <div className="absolute right-8 top-4">
          <Button variant="outline" size="sm" onClick={handleLogout}>
            <LogOut className="mr-1 h-4 w-4" />
            管理者登出
          </Button>
        </div>
        <Outlet />
      </main>
    </div>
  );
}
