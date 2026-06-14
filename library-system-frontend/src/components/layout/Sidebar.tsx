import { Link, useRouterState } from "@tanstack/react-router";
import { BookCopy, BookOpen, CircleDollarSign, ClipboardList, LayoutDashboard, MessageSquare, Users } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { cn } from "@/lib/utils";

const NAV = [
  { to: "/admin", label: "總覽", icon: LayoutDashboard, exact: true },
  { to: "/admin/books", label: "書籍管理", icon: BookCopy, exact: false },
  { to: "/admin/users", label: "使用者管理", icon: Users, exact: false },
  { to: "/admin/records", label: "借閱紀錄管理", icon: ClipboardList, exact: false },
  { to: "/admin/penalties", label: "罰款管理", icon: CircleDollarSign, exact: false },
  { to: "/admin/reviews", label: "書評管理", icon: MessageSquare, exact: false },
] as const;

export function Sidebar() {
  const { user } = useAuth();
  const pathname = useRouterState({ select: (s) => s.location.pathname });


  return (
    <aside className="flex w-64 shrink-0 flex-col border-r border-sidebar-border bg-sidebar text-sidebar-foreground">
      <div className="flex h-16 items-center gap-2 border-b border-sidebar-border px-6 font-bold">
        <BookOpen className="h-5 w-5 text-sidebar-primary" />
        <span>管理後台</span>
      </div>
      <nav className="flex-1 space-y-1 px-3 py-4">
        {NAV.map((item) => {
          const active = item.exact ? pathname === item.to : pathname.startsWith(item.to);
          return (
            <Link
              key={item.to}
              to={item.to}
              className={cn(
                "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                active
                  ? "bg-sidebar-accent text-sidebar-accent-foreground"
                  : "text-sidebar-foreground/80 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground",
              )}
            >
              <item.icon className="h-4 w-4" />
              {item.label}
            </Link>
          );
        })}
      </nav>
      <div className="border-t border-sidebar-border p-4">
        <div className="mb-3 text-sm">
          <p className="font-medium">{user?.name ?? "管理員"}</p>
          <p className="text-xs text-sidebar-foreground/70">{user?.studentId}</p>
        </div>
      </div>
    </aside>
  );
}
