package com.yourteam.library;

import java.util.List;

import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.service.AdminService;

public class TestAdminService {

    public static void main(String[] args) {
        // 建立 AdminService 物件
        AdminService adminService = new AdminService();

        // =========================
        // 測試 1：查詢全部借閱紀錄
        // =========================
        List<BorrowRecord> allRecords = adminService.getAllBorrowRecords();

        System.out.println("AdminService - 全部借閱紀錄測試結果：");
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
        // 測試 2：停權使用者
        // 這裡先假設測試使用者 user_id = 1
        // =========================
        boolean suspendResult = adminService.suspendUser(1);
        System.out.println("\n停權結果: " + suspendResult);

        // =========================
        // 測試 3：復權使用者
        // =========================
        boolean activateResult = adminService.activateUser(1);
        System.out.println("復權結果: " + activateResult);
    }
}