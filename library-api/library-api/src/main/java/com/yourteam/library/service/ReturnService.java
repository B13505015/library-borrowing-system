package com.yourteam.library.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.yourteam.library.entity.Book;
import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BorrowRecordRepository;
import com.yourteam.library.repository.PenaltyRepository;
import com.yourteam.library.repository.ReservationRepository;
import com.yourteam.library.repository.UserRepository;

public class ReturnService {

    private BorrowRecordRepository borrowRecordRepository;
    private BookRepository bookRepository;
    private ReservationRepository reservationRepository;
    private PenaltyRepository penaltyRepository;
    private UserRepository userRepository;

    public ReturnService() {
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.bookRepository = new BookRepository();
        this.reservationRepository = new ReservationRepository();
        this.penaltyRepository = new PenaltyRepository();
        this.userRepository = new UserRepository();
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

        // 還書後通知下一位預約者（若有）
        boolean notified = reservationRepository.notifyNextReservation(record.getBookId());

        // 若逾期，建立罰款紀錄
        double fineAmount = 0;
        if (record.getDueDate() != null && LocalDateTime.now().isAfter(record.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), LocalDateTime.now());
            if (overdueDays <= 0) overdueDays = 1;
            var user = userRepository.findByUserId(record.getUserId());
            String role = user == null ? "NORMAL" : user.getRoleLevel();
            double perDay = userRepository.findOverdueFinePerDayByRoleLevel(role);
            fineAmount = overdueDays * perDay;
            penaltyRepository.createFine(record.getUserId(), recordId, fineAmount, "逾期歸還 " + overdueDays + " 天");
        }

        if (!updateBookSuccess) {
            return "RETURN_FAILED";
        }

        return "RETURN_SUCCESS|NOTIFIED=" + notified + "|FINE=" + fineAmount;
    }
}
