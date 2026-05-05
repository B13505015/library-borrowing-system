import { Link, useNavigate, useRouterState } from "@tanstack/react-router";
import { BookOpen, ClipboardList, Heart, Home, LogOut, MessageSquare } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/context/AuthContext";
import { cn } from "@/lib/utils";

const NAV_ITEMS = [
  { to: "/user", label: "首頁", icon: Home, exact: true },
  { to: "/user/books", label: "查詢書籍", icon: BookOpen, exact: false },
  { to: "/user/records", label: "我的借閱紀錄", icon: ClipboardList, exact: false },
  { to: "/user/favorites", label: "我的收藏", icon: Heart, exact: false },
  { to: "/user/reviews", label: "書評專區", icon: MessageSquare, exact: false },
] as const;

export function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const pathname = useRouterState({ select: (s) => s.location.pathname });

  const handleLogout = async () => {
    await logout();
    navigate({ to: "/login" });
  };

  return (
    <header className="sticky top-0 z-30 border-b border-border bg-card/95 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-7xl items-center gap-6 px-6">
        <Link to="/user" className="flex items-center gap-2 font-bold text-primary">
          <BookOpen className="h-5 w-5" />
          <span>圖書館借還書系統</span>
        </Link>
        <nav className="hidden flex-1 items-center gap-1 md:flex">
          {NAV_ITEMS.map((item) => {
            const active = item.exact ? pathname === item.to : pathname.startsWith(item.to);
            return (
              <Link
                key={item.to}
                to={item.to}
                className={cn(
                  "inline-flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                  active
                    ? "bg-primary/10 text-primary"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground",
                )}
              >
                <item.icon className="h-4 w-4" />
                {item.label}
              </Link>
            );
          })}
        </nav>
        <div className="ml-auto flex items-center gap-3">
          <div className="hidden text-right text-sm sm:block">
            <p className="font-medium">{user?.name}</p>
            <p className="text-xs text-muted-foreground">{user?.studentId}</p>
          </div>
          <Button variant="outline" size="sm" onClick={handleLogout}>
            <LogOut className="mr-1 h-4 w-4" />
            登出
          </Button>
        </div>
      </div>
    </header>
  );
}
