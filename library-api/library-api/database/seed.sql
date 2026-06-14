USE library_system;

-- 僅保留系統運作所需的展示設定。
-- 不在版本庫中建立管理員或使用者帳號，避免提交可登入的預設憑證或個人資料。
INSERT INTO loan_policies (role_level, max_active_loans, overdue_fine_per_day, reservation_priority, fine_grace_days)
VALUES
('NORMAL', 3, 5.00, 1, 0),
('VIP', 5, 2.00, 10, 1)
ON DUPLICATE KEY UPDATE
max_active_loans = VALUES(max_active_loans),
overdue_fine_per_day = VALUES(overdue_fine_per_day),
reservation_priority = VALUES(reservation_priority),
fine_grace_days = VALUES(fine_grace_days);
