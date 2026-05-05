CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(100) NOT NULL,
    user_email VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    date_of_birth DATE,
    grade VARCHAR(50),
    class_name VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_users_email (user_email)
);

CREATE TABLE IF NOT EXISTS admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    account VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admins_account (account)
);

CREATE TABLE IF NOT EXISTS books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    publish_year INT,
    edition VARCHAR(50),
    format VARCHAR(50),
    source VARCHAR(100),
    note TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_books_title (title),
    CHECK (status IN ('AVAILABLE', 'BORROWED'))
);

CREATE TABLE IF NOT EXISTS book_isbns (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    UNIQUE KEY uk_book_isbns_book_isbn (book_id, isbn),
    CONSTRAINT fk_book_isbns_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS book_authors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    author VARCHAR(255) NOT NULL,
    INDEX idx_book_authors_author (author),
    CONSTRAINT fk_book_authors_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS book_subjects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    INDEX idx_book_subjects_subject (subject),
    CONSTRAINT fk_book_subjects_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS borrow_records (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATETIME NOT NULL,
    due_date DATETIME NOT NULL,
    return_date DATETIME NULL,
    borrow_days INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_borrow_records_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_borrow_records_book FOREIGN KEY (book_id) REFERENCES books(book_id),
    CONSTRAINT chk_due_after_borrow CHECK (due_date >= borrow_date),
    INDEX idx_borrow_records_user_return (user_id, return_date),
    INDEX idx_borrow_records_book_borrow (book_id, borrow_date)
);
