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
