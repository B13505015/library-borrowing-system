package com.yourteam.library.service;

import java.time.LocalDateTime;

import com.yourteam.library.entity.Book;
import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BorrowRecordRepository;

public class ReturnService {

    // 建立 repository 物件，負責和資料庫溝通
    private BorrowRecordRepository borrowRecordRepository;
    private BookRepository bookRepository;

    // 建構子（constructor）
    public ReturnService() {
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.bookRepository = new BookRepository();
    }

    // 還書功能
    // 回傳值說明：
    // "RECORD_NOT_FOUND"    -> 找不到借閱紀錄
    // "BOOK_NOT_FOUND"      -> 找不到對應書籍
    // "ALREADY_RETURNED"    -> 這筆紀錄已經還過書
    // "RETURN_SUCCESS"      -> 還書成功
    // "RETURN_FAILED"       -> 更新失敗
    public String returnBook(int recordId) {

        // 先檢查借閱紀錄是否存在
        BorrowRecord record = borrowRecordRepository.findByRecordId(recordId);
        if (record == null) {
            return "RECORD_NOT_FOUND";
        }

        // 如果 returnDate 不為 null，代表已經還過書
        if (record.getReturnDate() != null) {
            return "ALREADY_RETURNED";
        }

        // 檢查這筆紀錄對應的書籍是否存在
        Book book = bookRepository.findByBookId(record.getBookId());
        if (book == null) {
            return "BOOK_NOT_FOUND";
        }

        // 建立現在時間，作為還書時間
        LocalDateTime now = LocalDateTime.now();

        // 先更新借閱紀錄的 return_date
        boolean updateReturnSuccess = borrowRecordRepository.updateReturnDate(recordId, now);
        if (!updateReturnSuccess) {
            return "RETURN_FAILED";
        }

        // 再把書籍狀態改回 AVAILABLE
        boolean updateBookSuccess = bookRepository.updateBookStatus(record.getBookId(), "AVAILABLE");
        if (!updateBookSuccess) {
            return "RETURN_FAILED";
        }

        // 全部成功則回傳還書成功
        return "RETURN_SUCCESS";
    }
}