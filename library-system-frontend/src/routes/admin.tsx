import { createFileRoute, Outlet, redirect } from "@tanstack/react-router";
import { Sidebar } from "@/components/layout/Sidebar";

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
  return (
    <div className="flex min-h-screen w-full bg-muted/30">
      <Sidebar />
      <main className="flex-1 overflow-x-hidden px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}
