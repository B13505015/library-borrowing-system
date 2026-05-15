package library_api.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.entity.Book;
import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.entity.User;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BorrowRecordRepository;
import com.yourteam.library.repository.UserRepository;

import library_api.dto.ApiResponse;
import library_api.dto.BorrowRecordResponse;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminDashboardController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public AdminDashboardController() {
        this.bookRepository = new BookRepository();
        this.userRepository = new UserRepository();
        this.borrowRecordRepository = new BorrowRecordRepository();
    }

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsResponse> getDashboardStats() {
        List<Book> books = bookRepository.findAllBooks();
        List<User> users = userRepository.findAllUsers();
        List<BorrowRecord> records = borrowRecordRepository.findAllRecords();

        int totalBooks = books.size();
        int borrowedCount = 0;
        for (Book book : books) {
            if ("BORROWED".equalsIgnoreCase(book.getStatus())) {
                borrowedCount++;
            }
        }

        int totalUsers = users.size();

        int overdueCount = countUniqueOverdue(records);

        DashboardStatsResponse response = new DashboardStatsResponse(
                totalBooks,
                borrowedCount,
                totalUsers,
                overdueCount
        );

        return new ApiResponse<>(true, response, "查詢總覽統計成功");
    }

    @GetMapping("/recent-borrows")
    public ApiResponse<List<BorrowRecordResponse>> getRecentBorrowRecords(
            @RequestParam(required = false, defaultValue = "6") int limit) {

        List<BorrowRecord> records = borrowRecordRepository.findAllRecords();
        records.sort(Comparator.comparing(BorrowRecord::getBorrowDate).reversed());

        List<BorrowRecordResponse> responseList = new ArrayList<>();

        for (BorrowRecord record : records) {
            if (responseList.size() >= limit) break;

            User user = userRepository.findByUserId(record.getUserId());
            Book book = bookRepository.findByBookId(record.getBookId());
            if (user == null || book == null) continue;

            responseList.add(new BorrowRecordResponse(
                    String.valueOf(record.getRecordId()),
                    user.getStudentNo(),
                    user.getName(),
                    book.getTitle(),
                    record.getBorrowDate() == null ? null : record.getBorrowDate().toString(),
                    record.getDueDate() == null ? null : record.getDueDate().toString(),
                    record.getReturnDate() == null ? null : record.getReturnDate().toString(),
                    computeStatus(record)
            ));
        }

        return new ApiResponse<>(true, responseList, "查詢最近借閱成功");
    }

    @GetMapping("/overdue-records")
    public ApiResponse<List<BorrowRecordResponse>> getOverdueRecords() {
        List<BorrowRecord> records = borrowRecordRepository.findAllRecords();
        List<BorrowRecordResponse> responseList = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();

        for (BorrowRecord record : records) {
            if (!isOverdue(record)) continue;

            User user = userRepository.findByUserId(record.getUserId());
            Book book = bookRepository.findByBookId(record.getBookId());
            if (user == null || book == null) continue;

            String overdueKey = buildOverdueKey(user, book);
            if (seenKeys.contains(overdueKey)) continue;
            seenKeys.add(overdueKey);

            responseList.add(new BorrowRecordResponse(
                    String.valueOf(record.getRecordId()),
                    user.getStudentNo(),
                    user.getName(),
                    book.getTitle(),
                    record.getBorrowDate() == null ? null : record.getBorrowDate().toString(),
                    record.getDueDate() == null ? null : record.getDueDate().toString(),
                    record.getReturnDate() == null ? null : record.getReturnDate().toString(),
                    "OVERDUE"
            ));
        }

        return new ApiResponse<>(true, responseList, "查詢逾期紀錄成功");
    }


    private int countUniqueOverdue(List<BorrowRecord> records) {
        Set<String> seenKeys = new HashSet<>();
        for (BorrowRecord record : records) {
            if (!isOverdue(record)) continue;

            User user = userRepository.findByUserId(record.getUserId());
            Book book = bookRepository.findByBookId(record.getBookId());
            if (user == null || book == null) continue;

            seenKeys.add(buildOverdueKey(user, book));
        }
        return seenKeys.size();
    }

    private String buildOverdueKey(User user, Book book) {
        return user.getStudentNo() + "::" + book.getTitle();
    }

    private boolean isOverdue(BorrowRecord record) {
        return record.getReturnDate() == null
                && record.getDueDate() != null
                && record.getDueDate().isBefore(LocalDateTime.now());
    }

    private String computeStatus(BorrowRecord record) {
        if (record.getReturnDate() != null) return "RETURNED";
        if (isOverdue(record)) return "OVERDUE";
        return "BORROWED";
    }

    public static class DashboardStatsResponse {
        private int totalBooks;
        private int borrowedCount;
        private int totalUsers;
        private int overdueCount;

        public DashboardStatsResponse() {
        }

        public DashboardStatsResponse(int totalBooks, int borrowedCount, int totalUsers, int overdueCount) {
            this.totalBooks = totalBooks;
            this.borrowedCount = borrowedCount;
            this.totalUsers = totalUsers;
            this.overdueCount = overdueCount;
        }

        public int getTotalBooks() {
            return totalBooks;
        }

        public void setTotalBooks(int totalBooks) {
            this.totalBooks = totalBooks;
        }

        public int getBorrowedCount() {
            return borrowedCount;
        }

        public void setBorrowedCount(int borrowedCount) {
            this.borrowedCount = borrowedCount;
        }

        public int getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public int getOverdueCount() {
            return overdueCount;
        }

        public void setOverdueCount(int overdueCount) {
            this.overdueCount = overdueCount;
        }
    }
}