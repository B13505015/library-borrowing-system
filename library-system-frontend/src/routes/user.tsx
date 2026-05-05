import { createFileRoute, Outlet, redirect } from "@tanstack/react-router";
import { Navbar } from "@/components/layout/Navbar";

function getUserFromStorage() {
  if (typeof window === "undefined") return null;
  try {
    const raw = window.localStorage.getItem("auth_user");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export const Route = createFileRoute("/user")({
  beforeLoad: () => {
    const u = getUserFromStorage();
    if (!u) throw redirect({ to: "/login" });
    if (u.role !== "USER") throw redirect({ to: "/admin" });
  },
  component: UserLayout,
});

function UserLayout() {
  return (
    <div className="min-h-screen bg-muted/30">
      <Navbar />
      <div className="mx-auto max-w-7xl px-6 py-8">
        <Outlet />
      </div>
    </div>
  );
}
