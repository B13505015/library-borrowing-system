package com.yourteam.library;

import java.util.List;

import com.yourteam.library.entity.Book;
import com.yourteam.library.service.BookService;

public class TestBookService {

    public static void main(String[] args) {
        // 建立 BookService 物件
        BookService bookService = new BookService();

        // =========================
        // 測試 1：取得全部書籍
        // =========================
        List<Book> bookList = bookService.getAllBooks();

        System.out.println("BookService - 全部書籍測試結果：");
        System.out.println("總筆數: " + bookList.size());

        for (Book book : bookList) {
            System.out.println("----------------------------");
            System.out.println("bookId: " + book.getBookId());
            System.out.println("title: " + book.getTitle());
            System.out.println("status: " + book.getStatus());
        }

        // =========================
        // 測試 2：依照 bookId 取得單一本書
        // =========================
        // 這裡先假設測試資料的 book_id = 1
        Book singleBook = bookService.getBookById(1);

        System.out.println("\nBookService - 單一本書測試結果：");

        if (singleBook != null) {
            System.out.println("bookId: " + singleBook.getBookId());
            System.out.println("title: " + singleBook.getTitle());
            System.out.println("publisher: " + singleBook.getPublisher());
            System.out.println("publishYear: " + singleBook.getPublishYear());
            System.out.println("edition: " + singleBook.getEdition());
            System.out.println("format: " + singleBook.getFormat());
            System.out.println("source: " + singleBook.getSource());
            System.out.println("note: " + singleBook.getNote());
            System.out.println("status: " + singleBook.getStatus());
        } else {
            System.out.println("查無資料");
        }
    }
}