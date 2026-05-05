# library-borrowing-system
Java + MySQL library borrowing system with user and admin functions


## Repository structure (current)
- `library-api/library-api`: **primary backend** (Spring Boot API runtime).
- `library-system-frontend`: frontend app.
- `library-tools/data-import-test`: 匯入與手動測試工具（由舊 backend 抽出並重新命名）。
- `database/raw`: 初始 JSON 原始資料。
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
