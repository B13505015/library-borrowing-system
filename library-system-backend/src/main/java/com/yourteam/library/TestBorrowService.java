package com.yourteam.library;

import com.yourteam.library.service.BorrowService;

public class TestBorrowService {

    public static void main(String[] args) {
        // 建立 BorrowService 物件
        BorrowService borrowService = new BorrowService();

        // 測試借書
        // 這裡先假設：
        // user_id = 1
        // book_id = 1
        // 借閱天數 = 7 天
        String result = borrowService.borrowBook(1, 1, 7);

        // 印出借書結果
        System.out.println("借書結果: " + result);
    }
}