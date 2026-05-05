package com.yourteam.library.service;

import java.util.List;

import com.yourteam.library.entity.Book;
import com.yourteam.library.repository.BookRepository;

public class BookService {

    // 建立 BookRepository 物件，負責和資料庫溝通
    private BookRepository bookRepository;

    // 建構子（constructor）
    public BookService() {
        this.bookRepository = new BookRepository();
    }

    // 取得所有書籍資料
    public List<Book> getAllBooks() {
        return bookRepository.findAllBooks();
    }

    // 根據 bookId 取得單一本書
    public Book getBookById(int bookId) {
        return bookRepository.findByBookId(bookId);
    }
    
    // 根據關鍵字搜尋書籍
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooksByKeyword(keyword);
    }
}