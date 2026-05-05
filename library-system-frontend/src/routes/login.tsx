import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState, type FormEvent } from "react";
import { BookOpen, Loader2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { useAuth } from "@/context/AuthContext";

export const Route = createFileRoute("/login")({
  head: () => ({
    meta: [
      { title: "登入 — 圖書館借還書系統" },
      { name: "description", content: "使用學號登入校園圖書館借還書系統。" },
    ],
  }),
  component: LoginPage,
});

function LoginPage() {
  const { loginAsUser, loginAsAdmin } = useAuth();
  const navigate = useNavigate();
  const [studentId, setStudentId] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<"user" | "admin" | null>(null);

  const validate = () => {
    if (!studentId.trim()) return "請輸入學號";
    if (password.length < 4) return "請輸入密碼（至少 4 字元）";
    return null;
  };

  const submit = async (role: "user" | "admin", e: FormEvent) => {
    e.preventDefault();
    const v = validate();
    if (v) return setError(v);
    setError(null);
    setLoading(role);
    try {
      if (role === "user") {
        await loginAsUser({ studentId: studentId.trim(), password });
        navigate({ to: "/user" });
      } else {
        await loginAsAdmin({ studentId: studentId.trim(), password });
        navigate({ to: "/admin" });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "登入失敗");
    } finally {
      setLoading(null);
    }
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-gradient-to-br from-background via-background to-accent/30 p-6">
      <Card className="w-full max-w-md shadow-lg">
        <CardContent className="p-8">
          <div className="mb-6 flex flex-col items-center text-center">
            <div className="mb-3 flex h-14 w-14 items-center justify-center rounded-xl bg-primary/10 text-primary">
              <BookOpen className="h-7 w-7" />
            </div>
            <h1 className="text-2xl font-bold tracking-tight">圖書館借還書系統</h1>
            <p className="mt-1 text-sm text-muted-foreground">Library Borrowing System</p>
          </div>

          <form className="space-y-4" onSubmit={(e) => submit("user", e)}>
            <div className="space-y-1.5">
              <Label htmlFor="studentId">學號</Label>
              <Input
                id="studentId"
                placeholder="例如 S10901001"
                value={studentId}
                onChange={(e) => setStudentId(e.target.value)}
                autoComplete="username"
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="password">密碼</Label>
              <Input
                id="password"
                type="password"
                placeholder="請輸入密碼"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
              />
            </div>

            {error && (
              <Alert variant="destructive">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            <div className="grid grid-cols-2 gap-2 pt-2">
              <Button type="submit" disabled={loading !== null}>
                {loading === "user" && <Loader2 className="mr-1 h-4 w-4 animate-spin" />}
                使用者登入
              </Button>
              <Button
                type="button"
                variant="secondary"
                disabled={loading !== null}
                onClick={(e) => submit("admin", e)}
              >
                {loading === "admin" && <Loader2 className="mr-1 h-4 w-4 animate-spin" />}
                管理員登入
              </Button>
            </div>
          </form>

          <p className="mt-6 text-center text-sm text-muted-foreground">
            還沒有帳號？
            <Link to="/register" className="ml-1 font-medium text-primary hover:underline">
              前往註冊
            </Link>
          </p>
        </CardContent>
      </Card>
    </main>
  );
}
