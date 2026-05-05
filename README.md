# library-borrowing-system
Java + MySQL library borrowing system with user and admin functions


## Repository structure (current)
- `library-api/library-api`: **primary backend** (Spring Boot API runtime).
- `library-system-frontend`: frontend app.
- `Database`: 歷史 schema/seed/raw data artifacts.


## Database Setup

本專案使用 MySQL。資料庫重建檔案位於 `library-api/library-api/database/`。

### 1) 建立資料庫與資料表

```sql
source library-api/library-api/database/schema.sql;
```

### 2) 匯入測試資料

```sql
source library-api/library-api/database/seed.sql;
```

### 3) 設定 API 連線

請調整 `library-api/library-api/src/main/resources/application.properties` 內的 MySQL 連線資訊。

> 請勿將 MySQL 實體資料檔、資料目錄與密碼提交到 Git。


### 4) Importer 位置

JSON importer 已整併到 API 模組：`library-api/library-api/src/main/java/com/yourteam/library/importer/`。
對應 JSON 放在：`library-api/library-api/src/main/resources/importer/`。


## 資料庫一鍵初始化

已提供 `scripts/init_db.sh`：會依序執行 `schema.sql`、`seed.sql`，再自動跑 `DataImportRunner` 匯入 `src/main/resources/importer/*.json`。

```bash
DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASS=your_password ./scripts/init_db.sh
```

另外已將重複的 `database/`（小寫）資料夾移除，統一保留：
- `library-api/library-api/database/`（schema/seed）
- `Database/`（raw-data 與文件）
