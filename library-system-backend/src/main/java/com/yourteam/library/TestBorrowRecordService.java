package com.yourteam.library;

import java.util.List;

import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.service.BorrowRecordService;

public class TestBorrowRecordService {

    public static void main(String[] args) {
        // 建立 BorrowRecordService 物件
        BorrowRecordService borrowRecordService = new BorrowRecordService();

        // =========================
        // 測試 1：取得全部借閱紀錄
        // =========================
        List<BorrowRecord> allRecords = borrowRecordService.getAllRecords();

        System.out.println("BorrowRecordService - 全部借閱紀錄測試結果：");
        System.out.println("總筆數: " + allRecords.size());

        for (BorrowRecord record : allRecords) {
            System.out.println("----------------------------");
            System.out.println("recordId: " + record.getRecordId());
            System.out.println("userId: " + record.getUserId());
            System.out.println("bookId: " + record.getBookId());
            System.out.println("borrowDate: " + record.getBorrowDate());
            System.out.println("dueDate: " + record.getDueDate());
            System.out.println("returnDate: " + record.getReturnDate());
            System.out.println("borrowDays: " + record.getBorrowDays());
            System.out.println("createdAt: " + record.getCreatedAt());
        }

        // =========================
        // 測試 2：依 userId 取得借閱紀錄
        // =========================
        // 這裡先假設測試使用者的 user_id = 1
        List<BorrowRecord> userRecords = borrowRecordService.getRecordsByUserId(1);

        System.out.println("\nBorrowRecordService - 指定使用者借閱紀錄測試結果：");
        System.out.println("總筆數: " + userRecords.size());

        for (BorrowRecord record : userRecords) {
            System.out.println("----------------------------");
            System.out.println("recordId: " + record.getRecordId());
            System.out.println("userId: " + record.getUserId());
            System.out.println("bookId: " + record.getBookId());
            System.out.println("borrowDate: " + record.getBorrowDate());
            System.out.println("dueDate: " + record.getDueDate());
            System.out.println("returnDate: " + record.getReturnDate());
            System.out.println("borrowDays: " + record.getBorrowDays());
            System.out.println("createdAt: " + record.getCreatedAt());
        }
    }
}