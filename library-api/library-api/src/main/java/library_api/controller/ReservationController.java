package library_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.entity.Book;
import com.yourteam.library.entity.User;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.ReservationRepository;
import com.yourteam.library.repository.UserRepository;

import library_api.dto.ApiResponse;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:8080")
public class ReservationController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    public ReservationController() {
        this.userRepository = new UserRepository();
        this.bookRepository = new BookRepository();
        this.reservationRepository = new ReservationRepository();
    }

    @PostMapping
    public ApiResponse<Boolean> createReservation(@RequestBody ReservationRequest request) {
        User user = userRepository.findByUserId(request.getUserId());
        if (user == null) return new ApiResponse<>(false, null, "找不到使用者");

        Book book = bookRepository.findByBookId(request.getBookId());
        if (book == null) return new ApiResponse<>(false, null, "找不到書籍");

        if (!"BORROWED".equalsIgnoreCase(book.getStatus())) {
            return new ApiResponse<>(false, null, "此書目前可借閱，請直接借書");
        }

        int priority = "VIP".equalsIgnoreCase(user.getRoleLevel()) ? 10 : 1;
        boolean success = reservationRepository.createReservation(request.getUserId(), request.getBookId(), priority);
        return success
                ? new ApiResponse<>(true, true, "預約成功")
                : new ApiResponse<>(false, null, "預約失敗");
    }

    public static class ReservationRequest {
        private int userId;
        private int bookId;

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public int getBookId() { return bookId; }
        public void setBookId(int bookId) { this.bookId = bookId; }
    }
}
