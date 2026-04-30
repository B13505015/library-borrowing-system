package com.yourteam.library;

import java.util.List;

import com.yourteam.library.entity.Book;
import com.yourteam.library.repository.BookRepository;

public class TestBookRepository {

    public static void main(String[] args) {
        // 建立 BookRepository 物件
        BookRepository bookRepository = new BookRepository();

        // =========================
        // 測試 1：查詢所有書籍
        // =========================
        List<Book> bookList = bookRepository.findAllBooks();

        System.out.println("查詢全部書籍結果：");
        System.out.println("總筆數: " + bookList.size());

        // 逐筆印出書籍資料
        for (Book book : bookList) {
            System.out.println("----------------------------");
            System.out.println("bookId: " + book.getBookId());
            System.out.println("title: " + book.getTitle());
            System.out.println("publisher: " + book.getPublisher());
            System.out.println("publishYear: " + book.getPublishYear());
            System.out.println("edition: " + book.getEdition());
            System.out.println("format: " + book.getFormat());
            System.out.println("source: " + book.getSource());
            System.out.println("note: " + book.getNote());
            System.out.println("status: " + book.getStatus());
        }

        // =========================
        // 測試 2：根據 bookId 查詢單一本書
        // =========================
        // 先假設剛剛插入的第一本書 book_id = 1
        Book singleBook = bookRepository.findByBookId(1);

        System.out.println("\n查詢單一本書結果：");

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