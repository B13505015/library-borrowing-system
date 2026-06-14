#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASS=你的密碼 ./database/init_db.sh

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-}"

API_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SCHEMA_SQL="$API_DIR/database/schema.sql"
SEED_SQL="$API_DIR/database/seed.sql"

# 檢查 SQL 檔案是否存在
if [ ! -f "$SCHEMA_SQL" ]; then
  echo "找不到 schema.sql: $SCHEMA_SQL"
  exit 1
fi

if [ ! -f "$SEED_SQL" ]; then
  echo "找不到 seed.sql: $SEED_SQL"
  exit 1
fi

# 1. 建立資料表
MYSQL_PWD="$DB_PASS" mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" < "$SCHEMA_SQL"

# 2. 設定 Java importer 使用的資料庫連線
export LIB_DB_URL="jdbc:mysql://$DB_HOST:$DB_PORT/library_system?useSSL=false&serverTimezone=Asia/Taipei&allowPublicKeyRetrieval=true&characterEncoding=utf8"
export LIB_DB_USER="$DB_USER"
export LIB_DB_PASSWORD="$DB_PASS"

# 3. 編譯並匯入 JSON 資料
cd "$API_DIR"

if [ -f "./mvnw" ]; then
  ./mvnw -q -DskipTests compile
  ./mvnw -q -DskipTests exec:java -Dexec.mainClass=com.yourteam.library.importer.DataImportRunner
else
  mvn -q -DskipTests compile
  mvn -q -DskipTests exec:java -Dexec.mainClass=com.yourteam.library.importer.DataImportRunner
fi

# 4. 匯入初始設定與測試資料
MYSQL_PWD="$DB_PASS" mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" < "$SEED_SQL"

echo "DB schema、seed、JSON import 初始化完成"
