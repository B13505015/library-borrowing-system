package com.yourteam.library.entity;

public class Book {

    // 書籍主鍵 ID（Primary Key）
    private int bookId;

    // 書名
    private String title;

    // 出版社
    private String publisher;

    // 出版年份
    private int publishYear;

    // 版本，例如初版、第二版
    private String edition;

    // 書籍格式，例如平裝、精裝、電子書
    private String format;

    // 資料來源（source）
    private String source;

    // 備註欄位（note）
    private String note;

    // 書籍狀態，例如 AVAILABLE、BORROWED、REMOVED
    private String status;

    // 無參數建構子（No-args constructor）
    public Book() {
    }

    // 全欄位建構子（All-args constructor）
    public Book(int bookId, String title, String publisher, int publishYear,
                String edition, String format, String source, String note, String status) {
        this.bookId = bookId;
        this.title = title;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.edition = edition;
        this.format = format;
        this.source = source;
        this.note = note;
        this.status = status;
    }

    // Getter / Setter methods

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}