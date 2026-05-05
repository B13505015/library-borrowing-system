CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_level ENUM('NORMAL','VIP') NOT NULL DEFAULT 'NORMAL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('ACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    UNIQUE KEY uk_users_student_no (student_no),
    INDEX idx_users_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admins_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    authors VARCHAR(255),
    subjects VARCHAR(255),
    publisher VARCHAR(255),
    publish_year VARCHAR(10),
    edition VARCHAR(50),
    format_desc VARCHAR(100),
    source VARCHAR(100),
    note TEXT,
    status ENUM('AVAILABLE','BORROWED') NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_books_title (title),
    INDEX idx_books_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS book_isbns (
    isbn_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    UNIQUE KEY uk_book_isbn (book_id, isbn),
    INDEX idx_book_isbn (isbn),
    CONSTRAINT fk_book_isbns_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS borrow_records (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATETIME NOT NULL,
    due_date DATETIME NOT NULL,
    return_date DATETIME NULL,
    borrow_days INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_borrow_records_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_borrow_records_book FOREIGN KEY (book_id) REFERENCES books(book_id),
    CONSTRAINT chk_due_after_borrow CHECK (due_date >= borrow_date),
    CONSTRAINT chk_borrow_days_non_negative CHECK (borrow_days >= 0),
    INDEX idx_borrow_user_return (user_id, return_date),
    INDEX idx_borrow_book_borrow (book_id, borrow_date),
    INDEX idx_borrow_due_date (due_date)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS loan_policies (
    role_level ENUM('NORMAL','VIP') PRIMARY KEY,
    max_active_loans INT NOT NULL,
    overdue_fine_per_day DECIMAL(10,2) NOT NULL,
    reservation_priority INT NOT NULL,
    fine_grace_days INT NOT NULL DEFAULT 0,
    CONSTRAINT chk_max_active_loans_positive CHECK (max_active_loans > 0),
    CONSTRAINT chk_overdue_fine_non_negative CHECK (overdue_fine_per_day >= 0),
    CONSTRAINT chk_fine_grace_days_non_negative CHECK (fine_grace_days >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    status ENUM('WAITING','NOTIFIED','FULFILLED','CANCELLED','EXPIRED') NOT NULL DEFAULT 'WAITING',
    queue_priority INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notified_at DATETIME NULL,
    expires_at DATETIME NULL,
    CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_reservations_book FOREIGN KEY (book_id) REFERENCES books(book_id),
    INDEX idx_reservations_book_status_priority (book_id, status, queue_priority, created_at),
    INDEX idx_reservations_user_status (user_id, status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS favorites (
    favorite_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_favorites_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    UNIQUE KEY uk_favorites_user_book (user_id, book_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_penalties (
    penalty_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    borrow_record_id INT NULL,
    penalty_type ENUM('WARNING','FINE') NOT NULL,
    amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    reason VARCHAR(255),
    status ENUM('OPEN','PAID','WAIVED') NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_penalties_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_penalties_borrow_record FOREIGN KEY (borrow_record_id) REFERENCES borrow_records(record_id),
    CONSTRAINT chk_penalty_amount_non_negative CHECK (amount >= 0),
    INDEX idx_penalties_user_status (user_id, status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_suspensions (
    suspension_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    start_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_at DATETIME NULL,
    reason VARCHAR(255),
    created_by_admin INT NULL,
    CONSTRAINT fk_suspensions_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_suspensions_admin FOREIGN KEY (created_by_admin) REFERENCES admins(admin_id),
    INDEX idx_suspensions_user_active (user_id, is_active)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_record_id INT NULL,
    rating TINYINT NOT NULL,
    comment TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_reviews_book FOREIGN KEY (book_id) REFERENCES books(book_id),
    CONSTRAINT fk_reviews_borrow_record FOREIGN KEY (borrow_record_id) REFERENCES borrow_records(record_id),
    CONSTRAINT chk_rating_range CHECK (rating BETWEEN 1 AND 5),
    INDEX idx_reviews_book_created (book_id, created_at)
) ENGINE=InnoDB;
