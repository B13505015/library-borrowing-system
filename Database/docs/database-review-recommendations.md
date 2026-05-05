# 資料庫檢視與建議修改清單

檢視後發現目前 `Database/schema/*.sql` 幾乎皆為空檔（0 行），表示資料庫初始化與約束規則尚未落地，這是最優先要補齊的部分。

## P0（立即處理）

1. **補齊完整 DDL 腳本（建表）**
   - 在 `00_create_database.sql` 到 `08_indexes_constraints.sql` 建立可重複執行（idempotent）的 SQL。
   - 建議使用 `CREATE TABLE IF NOT EXISTS`。

2. **補齊主鍵、外鍵、唯一鍵與必要 CHECK**
   - `borrow_records.user_id` -> `users.id`（FK）
   - `borrow_records.book_id` -> `books.id`（FK）
   - `book_isbns` 對 `book_id + isbn` 設唯一鍵，避免重複 ISBN。
   - `borrow_records` 增加日期檢核（`due_date >= borrow_date`）。

3. **統一字元集與排序規則**
   - 建議全庫採 `utf8mb4`，避免書名、作者、中文欄位出現亂碼。

4. **建立 Migration 版本化機制**
   - 將 `schema`/`seed` 改成版本式（例如 Flyway/Liquibase 或手動 `V001__...sql`）。

## P1（高優先）

1. **補齊索引策略（查詢導向）**
   - `books(title)`：書名搜尋。
   - `borrow_records(user_id, return_date)`：查使用者借閱與未歸還清單。
   - `borrow_records(book_id, borrow_date)`：查書籍借閱歷史。

2. **稽核欄位標準化**
   - 每張核心表加 `created_at`, `updated_at`。
   - 若需要軟刪除，可加 `deleted_at`。

3. **欄位型別與 nullability 明確化**
   - 日期時間統一 `DATETIME` 或 `TIMESTAMP`（依時區需求擇一）。
   - 關鍵業務欄位（如 `users.email`, `books.title`）應 `NOT NULL`。

## P2（中期改善）

1. **狀態欄位正規化**
   - 借閱狀態可由 `returned_at IS NULL` 推導，避免重複儲存「可推導欄位」。

2. **避免重複資料與命名一致性**
   - 若存在多套 backend（`library-system-backend` 與 `library-api`），應統一資料庫 schema 來源，避免兩邊維護分歧。

3. **建立資料品質防線**
   - 匯入前 staging table + 驗證規則（必填、格式、重複鍵）。

## 建議先做的 3 個具體步驟

1. 先實作 `users/books/borrow_records` 三張核心表與對應 FK。
2. 針對前後台既有 API 查詢路徑補上索引（先以慢查詢 log 驗證）。
3. 加一份「初始化 smoke test SQL」或 CI 檢查，確保新環境可一鍵建庫與 seed。
