package library_api.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/api/records/user")
@CrossOrigin(origins = "http://localhost:8080")
public class UserBorrowRecordController {

    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;

    public UserBorrowRecordController() {
        this.userRepository = new UserRepository();
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.bookRepository = new BookRepository();
    }

    @GetMapping("/{studentId}")
    public ApiResponse<List<BorrowRecordResponse>> getUserBorrowRecords(@PathVariable String studentId) {

        User user = userRepository.findByStudentNo(studentId);

        if (user == null) {
            return new ApiResponse<>(false, null, "找不到使用者");
        }

        List<BorrowRecord> records = borrowRecordRepository.findRecordsByUserId(user.getUserId());
        List<BorrowRecordResponse> responseList = new ArrayList<>();

        for (BorrowRecord record : records) {
            Book book = bookRepository.findByBookId(record.getBookId());
            if (book == null) {
                continue;
            }

            String computedStatus = computeStatus(record);

            responseList.add(new BorrowRecordResponse(
                    String.valueOf(record.getRecordId()),
                    user.getStudentNo(),
                    user.getName(),
                    book.getTitle(),
                    record.getBorrowDate() == null ? null : record.getBorrowDate().toString(),
                    record.getDueDate() == null ? null : record.getDueDate().toString(),
                    record.getReturnDate() == null ? null : record.getReturnDate().toString(),
                    computedStatus
            ));
        }

        return new ApiResponse<>(true, responseList, "查詢個人借閱紀錄成功");
    }

    private String computeStatus(BorrowRecord record) {
        if (record.getReturnDate() != null) {
            return "RETURNED";
        }
        if (record.getDueDate() != null && record.getDueDate().isBefore(LocalDateTime.now())) {
            return "OVERDUE";
        }
        return "BORROWED";
    }
}