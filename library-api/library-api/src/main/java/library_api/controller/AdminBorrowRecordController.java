package library_api.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@RequestMapping("/api/admin/records")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminBorrowRecordController {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public AdminBorrowRecordController() {
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.userRepository = new UserRepository();
        this.bookRepository = new BookRepository();
    }

    @GetMapping
    public ApiResponse<List<BorrowRecordResponse>> getBorrowRecords(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status) {

        List<BorrowRecord> records = borrowRecordRepository.findAllRecords();
        List<BorrowRecordResponse> responseList = new ArrayList<>();

        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();

        for (BorrowRecord record : records) {
            User user = userRepository.findByUserId(record.getUserId());
            Book book = bookRepository.findByBookId(record.getBookId());

            if (user == null || book == null) {
                continue;
            }

            String computedStatus = computeStatus(record);

            boolean keywordMatched =
                    lowerKeyword.isEmpty()
                    || user.getStudentNo().toLowerCase().contains(lowerKeyword)
                    || user.getName().toLowerCase().contains(lowerKeyword)
                    || book.getTitle().toLowerCase().contains(lowerKeyword);

            boolean statusMatched =
                    "ALL".equalsIgnoreCase(status)
                    || computedStatus.equalsIgnoreCase(status);

            if (keywordMatched && statusMatched) {
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
        }

        return new ApiResponse<>(true, responseList, "查詢借閱紀錄成功");
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