import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState, type FormEvent } from "react";
import { Plus, Pencil, Trash2, Loader2, Eye } from "lucide-react";
import { toast } from "sonner";
import { PageHeader } from "@/components/layout/PageHeader";
import { SearchBar } from "@/components/common/SearchBar";
import { LoadingState } from "@/components/common/LoadingState";
import { ErrorState } from "@/components/common/ErrorState";
import { EmptyState } from "@/components/common/EmptyState";
import { StatusBadge } from "@/components/common/StatusBadge";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle,
} from "@/components/ui/dialog";
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { useAsync } from "@/hooks/useAsync";
import {
  searchBooks,
  handleAddBook,
  handleEditBook,
  handleRemoveBook,
  getBookBorrowHistory,
} from "@/services/bookService";
import type { Book, BookFormValues } from "@/types/book";
import type { BorrowRecord } from "@/types/borrowRecord";
import { formatDate } from "@/lib/format";

export const Route = createFileRoute("/admin/books")({
  head: () => ({ meta: [{ title: "書籍管理 — 圖書館借還書系統" }] }),
  component: AdminBooksPage,
});

const EMPTY_FORM: BookFormValues = {
  title: "",
  publisher: "",
  publishYear: new Date().getFullYear(),
  edition: "",
  format: "平裝",
  source: "校內採購",
  note: "",
};

function AdminBooksPage() {
  const [keyword, setKeyword] = useState("");
  const [editing, setEditing] = useState<Book | null>(null);
  const [creating, setCreating] = useState(false);
  const [removing, setRemoving] = useState<Book | null>(null);
  const [detail, setDetail] = useState<Book | null>(null);
  const [history, setHistory] = useState<BorrowRecord[]>([]);
  const [historyLoading, setHistoryLoading] = useState(false);

  const { data, loading, error, refetch } = useAsync(
    () => searchBooks(keyword).then((r) => r.data),
    [keyword],
  );

  const showBookDetail = async (book: Book) => {
    setDetail(book);
    setHistory([]);
    setHistoryLoading(true);

    try {
      const res = await getBookBorrowHistory(book.id);
      setHistory(res.data);
    } catch {
      setHistory([]);
    } finally {
      setHistoryLoading(false);
    }
  };

  const onRemove = async () => {
    if (!removing) return;
    try {
      await handleRemoveBook(removing.id);
      toast.success(`已下架《${removing.title}》`);
      setRemoving(null);
      refetch();
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "下架失敗");
    }
  };

  return (
    <>
      <PageHeader
        title="書籍管理"
        description="新增、編輯、下架館藏書籍。"
        actions={
          <Button onClick={() => setCreating(true)}>
            <Plus className="mr-1 h-4 w-4" />
            新增書籍
          </Button>
        }
      />

      <Card className="mb-4">
        <CardContent className="p-4">
          <SearchBar placeholder="搜尋書名、出版社、編號..." defaultValue={keyword} onSearch={setKeyword} />
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-0">
          {loading ? (
            <LoadingState />
          ) : error ? (
            <ErrorState message={error} onRetry={refetch} />
          ) : !data || data.length === 0 ? (
            <EmptyState title="找不到符合的書籍" />
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[100px]">編號</TableHead>
                  <TableHead>書名</TableHead>
                  <TableHead>出版社</TableHead>
                  <TableHead className="w-[100px]">出版年</TableHead>
                  <TableHead className="w-[100px]">格式</TableHead>
                  <TableHead className="w-[100px]">狀態</TableHead>
                  <TableHead className="w-[220px] text-right">操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {data.map((b) => (
                  <TableRow key={b.id}>
                    <TableCell className="font-mono text-xs">{b.id}</TableCell>
                    <TableCell className="font-medium">{b.title}</TableCell>
                    <TableCell>{b.publisher}</TableCell>
                    <TableCell>{b.publishYear}</TableCell>
                    <TableCell>{b.format}</TableCell>
                    <TableCell>
                      <StatusBadge status={b.status} />
                    </TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="sm" onClick={() => showBookDetail(b)}>
                        <Eye className="mr-1 h-4 w-4" />
                        詳情
                      </Button>

                      <Button variant="ghost" size="sm" onClick={() => setEditing(b)}>
                        <Pencil className="mr-1 h-4 w-4" />
                        編輯
                      </Button>

                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-destructive hover:text-destructive"
                        onClick={() => setRemoving(b)}
                      >
                        <Trash2 className="mr-1 h-4 w-4" />
                        下架
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      <BookFormDialog
        open={creating}
        onOpenChange={setCreating}
        title="新增書籍"
        initial={EMPTY_FORM}
        onSubmit={async (values) => {
          await handleAddBook(values);
          toast.success("已新增書籍");
          setCreating(false);
          refetch();
        }}
      />

      <BookFormDialog
        open={!!editing}
        onOpenChange={(o) => !o && setEditing(null)}
        title="編輯書籍"
        initial={editing ?? EMPTY_FORM}
        onSubmit={async (values) => {
          if (!editing) return;
          await handleEditBook(editing.id, values);
          toast.success("已更新書籍");
          setEditing(null);
          refetch();
        }}
      />

      <Dialog
        open={!!detail}
        onOpenChange={(o) => {
          if (!o) {
            setDetail(null);
            setHistory([]);
          }
        }}
      >
        <DialogContent className="max-h-[85vh] max-w-3xl overflow-hidden p-0">
          {detail && (
            <div className="flex max-h-[85vh] flex-col">
              <div className="border-b bg-background px-6 py-4">
                <DialogHeader>
                  <DialogTitle className="truncate pr-8">{detail.title}</DialogTitle>
                </DialogHeader>
              </div>

              <div className="flex-1 overflow-y-auto px-6 py-4">
                <div className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
                  <div>
                    <p className="text-xs text-muted-foreground">編號</p>
                    <p className="mt-1">{detail.id}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">出版社</p>
                    <p className="mt-1">{detail.publisher}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">出版年</p>
                    <p className="mt-1">{detail.publishYear}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">格式</p>
                    <p className="mt-1">{detail.format}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">版本</p>
                    <p className="mt-1">{detail.edition || "—"}</p>
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">狀態</p>
                    <div className="mt-1">
                      <StatusBadge status={detail.status} />
                    </div>
                  </div>
                  <div className="col-span-2">
                    <p className="text-xs text-muted-foreground">資料來源</p>
                    <p className="mt-1">{detail.source || "—"}</p>
                  </div>
                  <div className="col-span-2">
                    <p className="text-xs text-muted-foreground">附註</p>
                    <p className="mt-1">{detail.note || "—"}</p>
                  </div>
                </div>

                <div className="mt-4">
                  <h3 className="mb-2 text-sm font-semibold">近期借還紀錄</h3>

                  {historyLoading ? (
                    <p className="text-sm text-muted-foreground">載入中...</p>
                  ) : history.length === 0 ? (
                    <p className="text-sm text-muted-foreground">目前沒有借還紀錄。</p>
                  ) : (
                    <div className="max-h-64 overflow-y-auto rounded-md border">
                      <table className="w-full text-sm">
                        <thead className="sticky top-0 bg-muted/50">
                          <tr>
                            <th className="px-3 py-2 text-left">學號</th>
                            <th className="px-3 py-2 text-left">姓名</th>
                            <th className="px-3 py-2 text-left">借出時間</th>
                            <th className="px-3 py-2 text-left">到期時間</th>
                            <th className="px-3 py-2 text-left">歸還時間</th>
                            <th className="px-3 py-2 text-left">狀態</th>
                          </tr>
                        </thead>
                        <tbody>
                          {history.map((r) => (
                            <tr key={r.id} className="border-t">
                              <td className="px-3 py-2">{r.studentId}</td>
                              <td className="px-3 py-2">{r.studentName}</td>
                              <td className="px-3 py-2">{formatDate(r.borrowDate)}</td>
                              <td className="px-3 py-2">{formatDate(r.dueDate)}</td>
                              <td className="px-3 py-2">{formatDate(r.returnDate)}</td>
                              <td className="px-3 py-2">
                                <StatusBadge status={r.status} />
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              </div>

              <div className="border-t bg-background px-6 py-4">
                <DialogFooter>
                  <Button variant="outline" onClick={() => setDetail(null)}>
                    關閉
                  </Button>
                </DialogFooter>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      <AlertDialog open={!!removing} onOpenChange={(o) => !o && setRemoving(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>確定要下架此書？</AlertDialogTitle>
            <AlertDialogDescription>
              將永久移除《{removing?.title}》，此操作無法復原。
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>取消</AlertDialogCancel>
            <AlertDialogAction
              onClick={onRemove}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              確認下架
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}

function BookFormDialog({
  open,
  onOpenChange,
  title,
  initial,
  onSubmit,
}: {
  open: boolean;
  onOpenChange: (o: boolean) => void;
  title: string;
  initial: BookFormValues;
  onSubmit: (values: BookFormValues) => Promise<void>;
}) {
  const [values, setValues] = useState<BookFormValues>(initial);
  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState<Partial<Record<keyof BookFormValues, string>>>({});

  useEffect(() => {
    if (open) {
      setValues(initial);
      setErrors({});
    }
  }, [open, initial]);

  const update = <K extends keyof BookFormValues>(k: K, v: BookFormValues[K]) =>
    setValues((s) => ({ ...s, [k]: v }));

  const validate = () => {
    const e: typeof errors = {};
    if (!values.title.trim()) e.title = "請輸入書名";
    if (!values.publisher.trim()) e.publisher = "請輸入出版社";
    if (!values.publishYear || values.publishYear < 1900) e.publishYear = "出版年不正確";
    return e;
  };

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    const v = validate();
    setErrors(v);
    if (Object.keys(v).length) return;

    setSubmitting(true);
    try {
      await onSubmit(values);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "儲存失敗");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
        </DialogHeader>

        <form onSubmit={submit} className="grid grid-cols-2 gap-4">
          <FormField label="書名" error={errors.title} className="col-span-2">
            <Input
              value={values.title}
              onChange={(e) => update("title", e.target.value)}
            />
          </FormField>

          <FormField label="出版社" error={errors.publisher}>
            <Input
              value={values.publisher}
              onChange={(e) => update("publisher", e.target.value)}
            />
          </FormField>

          <FormField label="出版年" error={errors.publishYear}>
            <Input
              type="number"
              value={values.publishYear}
              onChange={(e) => update("publishYear", Number(e.target.value))}
            />
          </FormField>

          <FormField label="版本">
            <Input
              value={values.edition}
              onChange={(e) => update("edition", e.target.value)}
            />
          </FormField>

          <FormField label="格式">
            <Input
              value={values.format}
              onChange={(e) => update("format", e.target.value)}
            />
          </FormField>

          <FormField label="資料來源" className="col-span-2">
            <Input
              value={values.source}
              onChange={(e) => update("source", e.target.value)}
            />
          </FormField>

          <FormField label="附註" className="col-span-2">
            <Textarea
              value={values.note}
              onChange={(e) => update("note", e.target.value)}
              rows={3}
            />
          </FormField>

          <DialogFooter className="col-span-2">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              取消
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting && <Loader2 className="mr-1 h-4 w-4 animate-spin" />}
              儲存
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function FormField({
  label, error, className, children,
}: {
  label: string;
  error?: string;
  className?: string;
  children: React.ReactNode;
}) {
  return (
    <div className={className}>
      <Label className="mb-1.5 inline-block">{label}</Label>
      {children}
      {error && <p className="mt-1 text-xs text-destructive">{error}</p>}
    </div>
  );
}