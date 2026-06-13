package library_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.entity.Book;
import com.yourteam.library.entity.User;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.ReservationRepository;
import com.yourteam.library.repository.UserRepository;
import com.yourteam.library.repository.BorrowRecordRepository;
import com.yourteam.library.service.BorrowService;

import library_api.dto.ApiResponse;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowService borrowService;

    public ReservationController() {
        this.userRepository = new UserRepository();
        this.bookRepository = new BookRepository();
        this.reservationRepository = new ReservationRepository();
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.borrowService = new BorrowService();
    }

    @PutMapping("/{reservationId}/cancel")
    public ApiResponse<Boolean> cancelReservation(
            @PathVariable int reservationId,
            @RequestBody ReservationActionRequest request) {
        boolean success = reservationRepository.cancelReservation(reservationId, request.getUserId());
        return success
                ? new ApiResponse<>(true, true, "已取消預約")
                : new ApiResponse<>(false, null, "此預約無法取消或已更新");
    }

    @PostMapping("/{reservationId}/fulfill")
    public ApiResponse<Boolean> fulfillReservation(
            @PathVariable int reservationId,
            @RequestBody ReservationActionRequest request) {
        String result = borrowService.fulfillReservation(
                request.getUserId(), reservationId, request.getBorrowDays());
        switch (result) {
            case "BORROW_SUCCESS":
                return new ApiResponse<>(true, true, "借書成功");
            case "USER_NOT_FOUND":
                return new ApiResponse<>(false, null, "找不到使用者");
            case "USER_SUSPENDED":
                return new ApiResponse<>(false, null, "此帳號已被停權，無法借書");
            case "BORROW_LIMIT_REACHED":
                return new ApiResponse<>(false, null, "已達可同時借閱上限");
            case "BORROW_DAYS_NOT_ALLOWED":
                return new ApiResponse<>(false, null, "此身分不可選擇該借閱天數");
            case "RESERVATION_EXPIRED":
                return new ApiResponse<>(false, null, "預約已過期");
            case "BOOK_NOT_AVAILABLE":
                return new ApiResponse<>(false, null, "書籍目前不可借");
            case "RESERVATION_STATUS_CHANGED":
                return new ApiResponse<>(false, null, "預約狀態已更新");
            default:
                return new ApiResponse<>(false, null, "借閱失敗");
        }
    }

    @PostMapping
    public ApiResponse<Boolean> createReservation(@RequestBody ReservationRequest request) {
        User user = userRepository.findByUserId(request.getUserId());
        if (user == null) return new ApiResponse<>(false, null, "找不到使用者");

        Book book = bookRepository.findByBookId(request.getBookId());
        if (book == null) return new ApiResponse<>(false, null, "找不到書籍");

        if (borrowRecordRepository.hasActiveBorrowByUserAndBook(request.getUserId(), request.getBookId())) {
            return new ApiResponse<>(false, null, "你已借閱此書，無法重複預約");
        }

        if (!"BORROWED".equalsIgnoreCase(book.getStatus())) {
            return new ApiResponse<>(false, null, "此書目前可借閱，請直接借書");
        }

        if (reservationRepository.hasActiveReservationByTitle(request.getUserId(), request.getBookId())) {
            return new ApiResponse<>(false, null, "你已在預約隊列中");
        }

        int priority = "VIP".equalsIgnoreCase(user.getRoleLevel()) ? 10 : 1;
        boolean success = reservationRepository.createReservation(request.getUserId(), request.getBookId(), priority);
        if (success) return new ApiResponse<>(true, true, "預約成功");

        if (reservationRepository.hasActiveReservationByTitle(request.getUserId(), request.getBookId())) {
            return new ApiResponse<>(false, null, "你已在預約隊列中");
        }
        return new ApiResponse<>(false, null, "預約失敗");
    }

    public static class ReservationRequest {
        private int userId;
        private int bookId;

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public int getBookId() { return bookId; }
        public void setBookId(int bookId) { this.bookId = bookId; }
    }

    public static class ReservationActionRequest {
        private int userId;
        private int borrowDays = 7;

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public int getBorrowDays() { return borrowDays; }
        public void setBorrowDays(int borrowDays) { this.borrowDays = borrowDays; }
    }
}
