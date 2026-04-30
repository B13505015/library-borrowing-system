package com.yourteam.library.service;

import java.util.List;

import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.repository.BorrowRecordRepository;

public class BorrowRecordService {

    // 建立 BorrowRecordRepository 物件，負責和資料庫溝通
    private BorrowRecordRepository borrowRecordRepository;

    // 建構子（constructor）
    public BorrowRecordService() {
        this.borrowRecordRepository = new BorrowRecordRepository();
    }

    // 取得全部借閱紀錄
    public List<BorrowRecord> getAllRecords() {
        return borrowRecordRepository.findAllRecords();
    }

    // 根據 userId 取得某位使用者的借閱紀錄
    public List<BorrowRecord> getRecordsByUserId(int userId) {
        return borrowRecordRepository.findRecordsByUserId(userId);
    }
}