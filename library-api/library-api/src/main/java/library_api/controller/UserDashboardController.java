package library_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BorrowRecordRepository;
import com.yourteam.library.repository.UserRepository;
import com.yourteam.library.entity.User;

import library_api.dto.ApiResponse;
import library_api.dto.UserDashboardResponse;

@RestController
@RequestMapping("/api/user/dashboard")
@CrossOrigin(origins = "http://localhost:8080")
public class UserDashboardController {

    private final BookRepository bookRepository = new BookRepository();
    private final BorrowRecordRepository borrowRecordRepository = new BorrowRecordRepository();
    private final UserRepository userRepository = new UserRepository();

    @GetMapping("/{userId}")
    public ApiResponse<UserDashboardResponse> getDashboard(@PathVariable int userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) return new ApiResponse<>(false, null, "找不到使用者");

        int totalBooks = bookRepository.countAllBooks();
        int borrowedBooks = bookRepository.countBorrowedBooks();
        int availableBooks = Math.max(totalBooks - borrowedBooks, 0);

        int maxLoans = userRepository.findMaxActiveLoansByRoleLevel(user.getRoleLevel());
        int activeLoans = borrowRecordRepository.countActiveBorrowsByUserId(userId);
        int remainingQuota = Math.max(maxLoans - activeLoans, 0);

        return new ApiResponse<>(true,
                new UserDashboardResponse(totalBooks, borrowedBooks, availableBooks, remainingQuota),
                "查詢成功");
    }
}
