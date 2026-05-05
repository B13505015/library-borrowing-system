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
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:8080")
public class BookBorrowHistoryController {

    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;

    public BookBorrowHistoryController() {
        this.bookRepository = new BookRepository();
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.userRepository = new UserRepository();
    }

    @GetMapping("/{bookId}/history")
    public ApiResponse<List<BorrowRecordResponse>> getBookHistory(@PathVariable int bookId) {
        Book book = bookRepository.findByBookId(bookId);

        if (book == null) {
            return new ApiResponse<>(false, null, "找不到書籍");
        }

        List<BorrowRecord> records = borrowRecordRepository.findRecordsByBookId(bookId);
        List<BorrowRecordResponse> responseList = new ArrayList<>();

        for (BorrowRecord record : records) {
            User user = userRepository.findByUserId(record.getUserId());
            if (user == null) {
                continue;
            }

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

        return new ApiResponse<>(true, responseList, "查詢書籍借還紀錄成功");
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