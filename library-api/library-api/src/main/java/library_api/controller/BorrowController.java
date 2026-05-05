package library_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.service.BorrowService;

import library_api.dto.ApiResponse;
import library_api.dto.BorrowRequest;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "http://localhost:8080")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController() {
        this.borrowService = new BorrowService();
    }

    @PostMapping
    public ApiResponse<Boolean> borrowBook(@RequestBody BorrowRequest request) {

        System.out.println("borrow userId: " + request.getUserId());
        System.out.println("borrow bookId: " + request.getBookId());
        System.out.println("borrow days: " + request.getBorrowDays());

        String result = borrowService.borrowBook(
                request.getUserId(),
                request.getBookId(),
                request.getBorrowDays()
        );

        switch (result) {
            case "BORROW_SUCCESS":
                return new ApiResponse<>(true, true, "借書成功");
            case "USER_NOT_FOUND":
                return new ApiResponse<>(false, null, "找不到使用者");
            case "USER_SUSPENDED":
                return new ApiResponse<>(false, null, "此帳號已被停權，無法借書");
            case "BOOK_NOT_FOUND":
                return new ApiResponse<>(false, null, "找不到書籍");
            case "BOOK_NOT_AVAILABLE":
                return new ApiResponse<>(false, null, "此書目前不可借");
            case "BOOK_RESERVED":
                return new ApiResponse<>(true, true, "此書已被借出，已為你建立預約");
            case "BORROW_LIMIT_REACHED":
                return new ApiResponse<>(false, null, "已達可同時借閱上限");
            case "BORROW_DAYS_NOT_ALLOWED":
                return new ApiResponse<>(false, null, "此身分不可選擇該借閱天數");
            default:
                return new ApiResponse<>(false, null, "借書失敗");
        }
    }
}