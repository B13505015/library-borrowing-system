export interface AppUser {
  userId: number;
  studentId: string;
  name: string;
  level: "NORMAL" | "VIP";
  status: "ACTIVE" | "SUSPENDED" | "DISABLED";
  role: "USER" | "ADMIN";
}