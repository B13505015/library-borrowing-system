package com.yourteam.library.service;

import java.time.LocalDateTime;

import com.yourteam.library.entity.Book;
import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.entity.User;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BorrowRecordRepository;
import com.yourteam.library.repository.UserRepository;

public class BorrowService {

    // 建立 repository 物件，負責和資料庫溝通
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private BorrowRecordRepository borrowRecordRepository;
    
    private boolean isAllowedBorrowDays(String roleLevel, int borrowDays) {
        if ("VIP".equalsIgnoreCase(roleLevel)) {
            return borrowDays == 1 || borrowDays == 3 || borrowDays == 7 || borrowDays == 14;
        }

        // 舊資料如 "大學部" 也視同 NORMAL
        return borrowDays == 1 || borrowDays == 3 || borrowDays == 7;
    }

    // 建構子（constructor）
    public BorrowService() {
        this.userRepository = new UserRepository();
        this.bookRepository = new BookRepository();
        this.borrowRecordRepository = new BorrowRecordRepository();
    }

    // 借書功能
    // 回傳值說明：
    // "USER_NOT_FOUND"        -> 找不到使用者
    // "USER_SUSPENDED"        -> 使用者已被停權
    // "BOOK_NOT_FOUND"        -> 找不到書籍
    // "BOOK_NOT_AVAILABLE"    -> 書籍目前不可借
    // "BORROW_SUCCESS"        -> 借書成功
    // "BORROW_FAILED"         -> 借閱紀錄新增失敗或狀態更新失敗
    public String borrowBook(int userId, int bookId, int borrowDays) {

        // 先檢查使用者是否存在
        User user = findUserById(userId);
        if (user == null) {
            return "USER_NOT_FOUND";
        }

        // 檢查使用者是否被停權
        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            return "USER_SUSPENDED";
        }
        
        // 檢查借閱天數是否符合使用者等級
        if (!isAllowedBorrowDays(user.getRoleLevel(), borrowDays)) {
            return "BORROW_DAYS_NOT_ALLOWED";
        }

        // 檢查書籍是否存在
        Book book = bookRepository.findByBookId(bookId);
        if (book == null) {
            return "BOOK_NOT_FOUND";
        }

        // 檢查書籍是否可借
        if (!"AVAILABLE".equalsIgnoreCase(book.getStatus())) {
            return "BOOK_NOT_AVAILABLE";
        }

        // 建立借書時間與到期時間
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(borrowDays);

        // 建立新的借閱紀錄物件
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(now);
        record.setDueDate(dueDate);
        record.setReturnDate(null);
        record.setBorrowDays(borrowDays);
        record.setCreatedAt(now);

        // 先新增借閱紀錄
        boolean insertSuccess = borrowRecordRepository.insertBorrowRecord(record);
        if (!insertSuccess) {
            return "BORROW_FAILED";
        }

        // 再更新書籍狀態為 BORROWED
        boolean updateSuccess = bookRepository.updateBookStatus(bookId, "BORROWED");
        if (!updateSuccess) {
            return "BORROW_FAILED";
        }

        // 都成功則回傳借書成功
        return "BORROW_SUCCESS";
    }

    // 輔助方法：根據 userId 找使用者
    private User findUserById(int userId) {
        // 直接呼叫 UserRepository 的 findByUserId 方法
        return userRepository.findByUserId(userId);
    }
}