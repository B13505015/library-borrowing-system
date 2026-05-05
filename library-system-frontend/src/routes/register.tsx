import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState, type FormEvent } from "react";
import { Loader2, UserPlus } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { toast } from "sonner";
import { useAuth } from "@/context/AuthContext";

export const Route = createFileRoute("/register")({
  head: () => ({
    meta: [
      { title: "註冊 — 圖書館借還書系統" },
      { name: "description", content: "註冊圖書館借還書系統帳號。" },
    ],
  }),
  component: RegisterPage,
});

interface FormErrors {
  studentId?: string;
  name?: string;
  password?: string;
  confirmPassword?: string;
  global?: string;
}

function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [studentId, setStudentId] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState<FormErrors>({});
  const [loading, setLoading] = useState(false);
  const [level, setLevel] = useState<"NORMAL" | "VIP">("NORMAL");

  const validate = (): FormErrors => {
    const e: FormErrors = {};
    if (!studentId.trim()) e.studentId = "請輸入學號";
    else if (!/^[A-Za-z0-9]{4,20}$/.test(studentId.trim())) e.studentId = "學號格式不正確（4-20 碼英數字）";
    if (!name.trim()) e.name = "請輸入姓名";
    if (password.length < 6) e.password = "密碼長度至少 6 字元";
    if (confirmPassword !== password) e.confirmPassword = "兩次輸入的密碼不一致";
    return e;
  };

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    const v = validate();
    setErrors(v);
    if (Object.keys(v).length) return;
    setLoading(true);
    try {
      await register({
        studentId: studentId.trim(),
        name: name.trim(),
        password,
        level,
      });
      toast.success("註冊成功，請使用您的學號登入");
      navigate({ to: "/login" });
    } catch (err) {
      setErrors({ global: err instanceof Error ? err.message : "註冊失敗" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-gradient-to-br from-background via-background to-accent/30 p-6">
      <Card className="w-full max-w-md shadow-lg">
        <CardContent className="p-8">
          <div className="mb-6 flex flex-col items-center text-center">
            <div className="mb-3 flex h-14 w-14 items-center justify-center rounded-xl bg-primary/10 text-primary">
              <UserPlus className="h-7 w-7" />
            </div>
            <h1 className="text-2xl font-bold tracking-tight">建立新帳號</h1>
            <p className="mt-1 text-sm text-muted-foreground">註冊以使用圖書館服務</p>
          </div>

          <form className="space-y-4" onSubmit={submit} noValidate>
            <Field label="學號" id="studentId" error={errors.studentId}>
              <Input id="studentId" value={studentId} onChange={(e) => setStudentId(e.target.value)} placeholder="例如 S10901099" />
            </Field>
            <Field label="姓名" id="name" error={errors.name}>
              <Input id="name" value={name} onChange={(e) => setName(e.target.value)} placeholder="請輸入您的姓名" />
            </Field>
            <Field label="帳號類型" id="level">
              <select
                id="level"
                value={level}
                onChange={(e) => setLevel(e.target.value as "NORMAL" | "VIP")}
                className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
              >
                <option value="NORMAL">一般使用者</option>
                <option value="VIP">VIP 使用者</option>
              </select>
            </Field>            
            <Field label="密碼" id="password" error={errors.password}>
              <Input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="至少 6 字元" />
            </Field>
            <Field label="確認密碼" id="confirmPassword" error={errors.confirmPassword}>
              <Input id="confirmPassword" type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} placeholder="再次輸入密碼" />
            </Field>

            {errors.global && (
              <Alert variant="destructive">
                <AlertDescription>{errors.global}</AlertDescription>
              </Alert>
            )}

            <Button type="submit" className="w-full" disabled={loading}>
              {loading && <Loader2 className="mr-1 h-4 w-4 animate-spin" />}
              註冊
            </Button>
          </form>

          <p className="mt-6 text-center text-sm text-muted-foreground">
            已有帳號？
            <Link to="/login" className="ml-1 font-medium text-primary hover:underline">
              返回登入
            </Link>
          </p>
        </CardContent>
      </Card>
    </main>
  );
}

function Field({ label, id, error, children }: { label: string; id: string; error?: string; children: React.ReactNode }) {
  return (
    <div className="space-y-1.5">
      <Label htmlFor={id}>{label}</Label>
      {children}
      {error && <p className="text-xs text-destructive">{error}</p>}
    </div>
  );
}
