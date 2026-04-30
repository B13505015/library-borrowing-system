package com.yourteam.library.service;

import java.util.List;

import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.repository.BorrowRecordRepository;
import com.yourteam.library.repository.UserRepository;

public class AdminService {

    // 建立 repository 物件，負責和資料庫溝通
    private BorrowRecordRepository borrowRecordRepository;
    private UserRepository userRepository;

    // 建構子（constructor）
    public AdminService() {
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.userRepository = new UserRepository();
    }

    // 取得全部借閱紀錄
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAllRecords();
    }

    // 根據 userId 取得指定使用者的借閱紀錄
    public List<BorrowRecord> getBorrowRecordsByUserId(int userId) {
        return borrowRecordRepository.findRecordsByUserId(userId);
    }

    // 停權使用者
    // 成功回傳 true，失敗回傳 false
    public boolean suspendUser(int userId) {
        return userRepository.updateUserStatus(userId, "SUSPENDED");
    }

    // 復權使用者
    // 成功回傳 true，失敗回傳 false
    public boolean activateUser(int userId) {
        return userRepository.updateUserStatus(userId, "ACTIVE");
    }
}