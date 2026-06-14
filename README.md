# 圖書館借還書管理系統

線上展示網站：https://library-borrowing-system-ruby.vercel.app/

## 專案簡介

本專案是一套以課程專題為目的開發的圖書館借還書管理系統，提供一般使用者、VIP 使用者與管理員不同的操作介面。系統涵蓋館藏查詢、借閱、歸還、預約、收藏、書評、逾期罰款與管理統計等流程，前後端透過 REST API 溝通。

## 主要功能

- 完整館藏資料與多 ISBN 管理
- 書名、作者、主題、出版社、ISBN 與書籍編號搜尋
- 借閱、歸還與借閱紀錄查詢
- 預約排隊、到書通知、取消與立即借閱
- 收藏與書評
- 逾期罰款計算及模擬繳費
- 一般會員與 VIP 會員等級
- 管理員館藏、使用者、借閱、罰款與書評管理
- 主題借閱統計與熱門主題分析

## 使用者端功能

- 註冊、登入及選擇 NORMAL／VIP 會員
- 搜尋館藏並查看作者、主題、ISBN、出版資訊、近期借還紀錄與評論
- 借閱可用書籍，或對借出中的書籍建立預約
- 查看預約隊列與到書通知
- 收藏／取消收藏書籍
- 查看個人借閱紀錄與目前累積罰款
- 對已結算且未繳的罰款進行模擬付款
- 發表與查看書評

## 管理員端功能

- 查看館藏、借出、使用者與逾期概況
- 新增、編輯、下架及查詢書籍
- 管理使用者狀態與 NORMAL／VIP 會員等級
- 查詢借閱紀錄與逾期提醒
- 查看罰款，並將未繳罰款標記為已繳或免除
- 搜尋及刪除書評
- 查看熱門主題 Top 5、主題借閱長條圖與排行

## 技術架構

| 層級 | 技術 |
| --- | --- |
| 前端 | React 19、TypeScript、TanStack Start／Router、Vite、Tailwind CSS |
| 後端 | Java 17、Spring Boot 3、JDBC、Maven |
| 資料庫 | MySQL 8 |
| 前端部署 | Vercel |
| 後端／資料庫部署 | 可部署於支援 Java 與 MySQL 的雲端平台 |

## 專案結構

```text
library-borrowing-system/
├── library-api/
│   └── library-api/
│       ├── database/          # MySQL schema、展示 seed 與初始化腳本
│       ├── src/main/java/     # Spring Boot API
│       └── src/main/resources/
├── library-system-frontend/   # React／TanStack Start 前端
├── library-tools/             # 資料匯入相關工具
├── Database/                  # 原始資料與資料庫設計參考文件
└── README.md
```

## 本機執行方式

### 1. 環境需求

- Java 17
- Maven 3.9+
- Node.js 22.12+
- npm
- MySQL 8

### 2. 建立資料庫

SQL 檔案位於 `library-api/library-api/database/`：

```bash
mysql -u root -p < library-api/library-api/database/schema.sql
mysql -u root -p < library-api/library-api/database/seed.sql
```

也可使用初始化腳本。資料庫密碼只透過執行環境傳入，不會寫入儲存庫：

```bash
DB_HOST=127.0.0.1 \
DB_PORT=3306 \
DB_USER=root \
DB_PASS='your_local_password' \
./library-api/library-api/database/init_db.sh
```

初始化腳本會建立資料表、執行現有 JSON importer，再套用少量非個資的初始設定。

### 3. 啟動後端

後端透過下列環境變數取得 MySQL 連線資訊：

```bash
cd library-api/library-api
DB_HOST=127.0.0.1 \
DB_PORT=3306 \
DB_NAME=library_system \
DB_USER=root \
DB_PASSWORD='your_local_password' \
mvn spring-boot:run
```

預設 API port 為 `8080`；可透過 `PORT` 環境變數調整。

### 4. 啟動前端

```bash
cd library-system-frontend
npm install
VITE_API_BASE_URL=http://localhost:8080/api npm run dev
```

## 部署說明

- 前端可部署至 Vercel，專案 Root Directory 設為 `library-system-frontend`。
- 在前端部署環境設定 `VITE_API_BASE_URL`，指向已部署後端的 `/api` URL。
- 後端部署環境需設定 `PORT`、`DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER` 與 `DB_PASSWORD`。
- MySQL 連線資訊與其他機密資料應只存放於部署平台的 Environment Variables，不應提交至 Git。

## 資料庫說明

- `library-api/library-api/database/schema.sql`：可重建 MySQL 資料庫與目前系統使用的資料表。
- `library-api/library-api/database/seed.sql`：只包含必要的展示設定，不含真實使用者個資或線上資料庫 dump。
- `library-api/library-api/database/init_db.sh`：本機初始化輔助腳本。
- 主要資料表包含 `users`、`admins`、`books`、`book_isbns`、`borrow_records`、`reservations`、`favorites`、`reviews`、`user_penalties`、`user_suspensions` 與 `loan_policies`。
- `books.status` 支援 `AVAILABLE`、`BORROWED`、`REMOVED`；預約與罰款狀態定義請以 `schema.sql` 為準。

請勿提交資料庫密碼、雲端資料庫 host、`.env`、真實帳號資料或未去識別化的線上資料庫 dump。

## 備註

本系統為課程專題用途，VIP 註冊與罰款付款功能皆為模擬付款，不會進行真實金流交易。
