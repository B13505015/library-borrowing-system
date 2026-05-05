# 圖書館借還書系統 — 前端專案計畫

使用 TanStack Start + React + TypeScript + Tailwind + shadcn/ui，全繁體中文，desktop-first，正式校園後台風格（簡潔／專業／現代）。所有資料透過 service 層存取，目前回傳 mock data，未來可無痛替換為 Java 後端 API。

## 視覺風格

- 主色調：深藍（學術／圖書館感）+ 中性灰白底 + 強調色用於 badge 與 CTA
- 字型：系統 sans-serif，標題加粗
- 元件：圓角適中、陰影柔和、hover/focus/active 有微互動
- Badge 配色：AVAILABLE（綠）／BORROWED（藍）／OVERDUE（紅）／RETURNED（灰）／ACTIVE（綠）／SUSPENDED（紅）

## 頁面與路由

```text
/login                      登入頁（學號 + 密碼，使用者／管理員兩顆按鈕）
/register                   註冊頁
/user                       使用者首頁（歡迎、借閱中、逾期提醒、快捷入口）
/user/books                 書籍查詢（搜尋 + 表格 + 詳情 modal + 借閱）
/user/records               我的借閱紀錄（含還書按鈕、逾期紅標）
/admin                      管理員總覽（統計卡 + 最近活動 + 逾期提醒）
/admin/books                書籍管理（新增／編輯／下架 modal）
/admin/users                使用者管理（停權／復權）
/admin/records              借閱紀錄管理（依學號／書名搜尋）
```

使用者路由用上方導覽列；管理員路由用左側 Sidebar。兩者皆透過 `_authenticated` / `_admin` layout route 守衛（讀取 mock auth context）。

## 檔案結構

```text
src/
  routes/
    __root.tsx
    login.tsx
    register.tsx
    _user.tsx                       使用者 layout（Navbar + Outlet）
    _user/index.tsx                 → /user
    _user/books.tsx
    _user/records.tsx
    _admin.tsx                      管理員 layout（Sidebar + Outlet）
    _admin/index.tsx                → /admin
    _admin/books.tsx
    _admin/users.tsx
    _admin/records.tsx
  components/
    layout/Navbar.tsx
    layout/Sidebar.tsx
    layout/PageHeader.tsx
    common/StatusBadge.tsx
    common/StatCard.tsx
    common/SearchBar.tsx
    common/DataTable.tsx
    common/EmptyState.tsx
    common/ErrorState.tsx
    common/LoadingState.tsx
    common/ConfirmDialog.tsx
    books/BookDetailDialog.tsx
    books/BookFormDialog.tsx
  services/                         API 抽象層（目前回傳 mock，未來改 fetch）
    http.ts                         未來 axios/fetch 包裝
    authService.ts                  handleUserLogin / handleAdminLogin / handleRegister
    bookService.ts                  searchBooks / getBookById / addBook / editBook / removeBook
    borrowService.ts                getMyBorrowRecords / borrowBook / handleReturnBook
    adminService.ts                 fetchDashboardStats / suspendUser / activateUser / searchBorrowRecords
  mocks/
    books.ts
    users.ts
    borrowRecords.ts
    stats.ts
  types/                            對齊未來 Java DTO
    book.ts  user.ts  borrowRecord.ts  auth.ts  api.ts (ApiResponse<T>)
  context/
    AuthContext.tsx                 mock 登入狀態（localStorage）
  hooks/
    useAsync.ts                     統一 loading / error / data 狀態
  lib/
    format.ts                       日期、逾期判斷
```

## 資料型別（對齊未來 Java DTO）

```ts
type ApiResponse<T> = { success: boolean; data: T; message?: string };
type Book = { id; title; publisher; publishYear; edition; format; source; note; status: 'AVAILABLE'|'BORROWED' };
type User = { studentId; name; level; status: 'ACTIVE'|'SUSPENDED' };
type BorrowRecord = { id; studentId; studentName; bookId; bookTitle; borrowDate; dueDate; returnDate?; status: 'BORROWED'|'RETURNED'|'OVERDUE' };
```

所有 service 函式 signature 模擬真實 API（async、回傳 `ApiResponse<T>`、模擬 300ms 延遲、可拋錯），未來只要把函式內部換成 `http.get/post` 即可。

## 預留的函式命名（全部會出現在程式中）

`handleUserLogin`、`handleAdminLogin`、`handleRegister`、`fetchDashboardStats`、`renderRecentBorrowRecords`、`renderOverdueRecords`、`searchBooks`、`renderBookTable`、`showBookDetail`、`renderBorrowRecords`、`handleReturnBook`、`renderAdminBookTable`、`handleAddBook`、`handleEditBook`、`handleRemoveBook`、`renderAdminUserTable`、`handleSuspendUser`、`handleActivateUser`、`renderAdminBorrowTable`、`searchBorrowRecords`。

## 進階功能

- 表單驗證：使用 react-hook-form + zod（登入、註冊、書籍表單）
- 每個資料頁面三態：`<LoadingState/>`、`<EmptyState/>`、`<ErrorState onRetry/>`
- 共用 `DataTable`：欄位定義、搜尋、假分頁、操作欄
- Modal：書籍詳情、書籍新增／編輯、停權／下架確認
- Badge 元件統一狀態顏色
- Toast（sonner）顯示借書／還書／停權成功

## 未來串接 Java 後端

- 所有畫面只呼叫 `services/*`，不直接接觸 mock
- `services/http.ts` 預留 baseURL、token header、錯誤攔截
- 替換步驟：把 `bookService.searchBooks` 內的 mock filter 換成 `http.get('/api/books', { params })` 即可，UI 不需修改

## 交付內容

完整可執行專案：所有 9 個頁面、路由守衛、共用元件、mock 資料、互動流程（登入→借書→還書、管理員停權→復權、新增／編輯／下架書籍）。
