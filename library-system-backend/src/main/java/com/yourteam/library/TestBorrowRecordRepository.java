package com.yourteam.library;

import java.util.List;

import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.repository.BorrowRecordRepository;

public class TestBorrowRecordRepository {

    public static void main(String[] args) {
        // 建立 BorrowRecordRepository 物件
        BorrowRecordRepository borrowRecordRepository = new BorrowRecordRepository();

        // =========================
        // 測試 1：查詢全部借閱紀錄
        // =========================
        List<BorrowRecord> allRecords = borrowRecordRepository.findAllRecords();

        System.out.println("查詢全部借閱紀錄結果：");
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
        // 測試 2：查詢指定使用者的借閱紀錄
        // =========================
        // 這裡先假設測試使用者的 user_id = 1
        List<BorrowRecord> userRecords = borrowRecordRepository.findRecordsByUserId(1);

        System.out.println("\n查詢指定使用者借閱紀錄結果：");
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