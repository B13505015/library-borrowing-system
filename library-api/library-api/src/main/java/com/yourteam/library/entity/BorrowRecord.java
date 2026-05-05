package com.yourteam.library.entity;

import java.time.LocalDateTime;

public class BorrowRecord {

    // 借閱紀錄主鍵 ID（Primary Key）
    private int recordId;

    // 借書的使用者 ID（對應 users.user_id）
    private int userId;

    // 被借出的書籍 ID（對應 books.book_id）
    private int bookId;

    // 借書時間
    private LocalDateTime borrowDate;

    // 到期時間
    private LocalDateTime dueDate;

    // 還書時間；如果尚未歸還，可以是 null
    private LocalDateTime returnDate;

    // 借閱天數，例如 1、3、7、14
    private int borrowDays;

    // 建立紀錄時間
    private LocalDateTime createdAt;

    // 無參數建構子（No-args constructor）
    public BorrowRecord() {
    }

    // 全欄位建構子（All-args constructor）
    public BorrowRecord(int recordId, int userId, int bookId,
                        LocalDateTime borrowDate, LocalDateTime dueDate,
                        LocalDateTime returnDate, int borrowDays,
                        LocalDateTime createdAt) {
        this.recordId = recordId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.borrowDays = borrowDays;
        this.createdAt = createdAt;
    }

    // Getter / Setter methods

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public int getBorrowDays() {
        return borrowDays;
    }

    public void setBorrowDays(int borrowDays) {
        this.borrowDays = borrowDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}