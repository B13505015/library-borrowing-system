USE library_db;

INSERT INTO admins (account, password)
VALUES ('admin', 'admin123')
ON DUPLICATE KEY UPDATE account = VALUES(account);

INSERT INTO users (user_name, user_email, phone, grade, class_name)
VALUES
('王小明', 'ming@example.com', '0911111111', '一年級', 'A班'),
('陳小華', 'hua@example.com', '0922222222', '二年級', 'B班')
ON DUPLICATE KEY UPDATE user_name = VALUES(user_name);

INSERT INTO books (title, publisher, publish_year, status)
VALUES
('Java 程式設計', 'Tech Press', 2022, 'AVAILABLE'),
('資料庫系統', 'Data Press', 2021, 'AVAILABLE');
