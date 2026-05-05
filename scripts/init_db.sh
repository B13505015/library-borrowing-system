#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASS=secret ./scripts/init_db.sh

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-}"

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
API_DIR="$ROOT_DIR/library-api/library-api"
SCHEMA_SQL="$API_DIR/database/schema.sql"
SEED_SQL="$API_DIR/database/seed.sql"

MYSQL_PWD="$DB_PASS" mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" < "$SCHEMA_SQL"
MYSQL_PWD="$DB_PASS" mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" < "$SEED_SQL"

cd "$API_DIR"
./mvnw -q -DskipTests compile
./mvnw -q -DskipTests exec:java -Dexec.mainClass=com.yourteam.library.importer.DataImportRunner

echo "✅ DB schema/seed/import 初始化完成"
