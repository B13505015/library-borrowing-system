USE library_system;

INSERT INTO admins (username, password)
VALUES ('admin', '0000')
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


INSERT INTO loan_policies (role_level, max_active_loans, overdue_fine_per_day, reservation_priority, fine_grace_days)
VALUES
('NORMAL', 3, 5.00, 1, 0),
('VIP', 5, 2.00, 10, 1)
ON DUPLICATE KEY UPDATE
max_active_loans = VALUES(max_active_loans),
overdue_fine_per_day = VALUES(overdue_fine_per_day),
reservation_priority = VALUES(reservation_priority),
fine_grace_days = VALUES(fine_grace_days);

INSERT IGNORE INTO favorites (user_id, book_id)
SELECT DISTINCT br.user_id, br.book_id
FROM borrow_records br
WHERE br.user_id IS NOT NULL AND br.book_id IS NOT NULL
LIMIT 20;

INSERT INTO reviews (user_id, book_id, borrow_record_id, rating, comment)
SELECT br.user_id, br.book_id, br.record_id,
       CASE WHEN br.return_date IS NULL THEN 4 ELSE 5 END AS rating,
       CASE WHEN br.return_date IS NULL THEN '閱讀中，先給個好評' ELSE '已讀完，內容不錯' END AS comment
FROM borrow_records br
LEFT JOIN reviews r ON r.borrow_record_id = br.record_id
WHERE r.review_id IS NULL
LIMIT 20;
