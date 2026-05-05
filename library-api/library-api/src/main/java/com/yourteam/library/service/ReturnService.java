package com.yourteam.library.service;

import java.time.LocalDateTime;

import com.yourteam.library.entity.Book;
import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BorrowRecordRepository;

public class ReturnService {

    private BorrowRecordRepository borrowRecordRepository;
    private BookRepository bookRepository;

    public ReturnService() {
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.bookRepository = new BookRepository();
    }

    // 回傳值：
    // RECORD_NOT_FOUND -> 找不到借閱紀錄
    // ALREADY_RETURNED -> 已還書
    // BOOK_NOT_FOUND   -> 找不到書籍
    // RETURN_SUCCESS   -> 還書成功
    // RETURN_FAILED    -> 還書失敗
    public String returnBook(int recordId) {

        BorrowRecord record = borrowRecordRepository.findByRecordId(recordId);
        if (record == null) {
            return "RECORD_NOT_FOUND";
        }

        if (record.getReturnDate() != null) {
            return "ALREADY_RETURNED";
        }

        Book book = bookRepository.findByBookId(record.getBookId());
        if (book == null) {
            return "BOOK_NOT_FOUND";
        }

        boolean updateReturnSuccess = borrowRecordRepository.updateReturnDate(
                recordId,
                LocalDateTime.now()
        );

        if (!updateReturnSuccess) {
            return "RETURN_FAILED";
        }

        boolean updateBookSuccess = bookRepository.updateBookStatus(record.getBookId(), "AVAILABLE");

        if (!updateBookSuccess) {
            return "RETURN_FAILED";
        }

        return "RETURN_SUCCESS";
    }
}