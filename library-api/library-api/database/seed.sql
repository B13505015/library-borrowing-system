USE library_db;

INSERT INTO admins (username, password)
VALUES ('admin', 'admin123')
ON DUPLICATE KEY UPDATE
password = VALUES(password),
updated_at = CURRENT_TIMESTAMP;

INSERT INTO users (student_no, name, password, role_level, status)
VALUES
('S0001', '王小明', 'pass123', 'NORMAL', 'ACTIVE'),
('S0002', '陳小華', 'pass456', 'VIP', 'ACTIVE')
ON DUPLICATE KEY UPDATE
name = VALUES(name),
password = VALUES(password),
role_level = VALUES(role_level),
status = VALUES(status),
updated_at = CURRENT_TIMESTAMP;

INSERT INTO books (title, authors, subjects, publisher, publish_year, edition, format_desc, source, note, status)
VALUES
('Java 程式設計', '作者A', '程式設計', 'Tech Press', '2022', '1st', '平裝', 'seed', '', 'AVAILABLE'),
('資料庫系統', '作者B', '資料庫', 'Data Press', '2021', '2nd', '平裝', 'seed', '', 'BORROWED')
ON DUPLICATE KEY UPDATE
status = VALUES(status),
updated_at = CURRENT_TIMESTAMP;
