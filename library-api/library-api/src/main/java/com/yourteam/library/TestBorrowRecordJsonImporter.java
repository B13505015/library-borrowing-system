package com.yourteam.library;

import com.yourteam.library.importer.BorrowRecordJsonImporter;

public class TestBorrowRecordJsonImporter {

    public static void main(String[] args) {
        // 建立 BorrowRecordJsonImporter 物件
        BorrowRecordJsonImporter importer = new BorrowRecordJsonImporter();

        // 正式匯入 Borrow_records.json 到資料庫
        importer.importBorrowRecordsJson();
    }
}