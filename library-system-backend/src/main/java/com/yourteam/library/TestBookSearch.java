package com.yourteam.library;

import java.util.List;

import com.yourteam.library.entity.Book;
import com.yourteam.library.service.BookService;

public class TestBookSearch {

    public static void main(String[] args) {
        // 建立 BookService 物件
        BookService bookService = new BookService();

        // 測試搜尋關鍵字
        // 你可以先用「倫理」、「哲學」、「聯經」之類的字測試
        String keyword = "倫理";

        // 呼叫搜尋方法
        List<Book> resultList = bookService.searchBooks(keyword);

        // 印出搜尋結果
        System.out.println("搜尋關鍵字: " + keyword);
        System.out.println("找到筆數: " + resultList.size());

        for (Book book : resultList) {
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
    }
}