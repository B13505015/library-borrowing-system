package library_api.controller;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yourteam.library.entity.BorrowRecord;
import com.yourteam.library.entity.Book;
import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BorrowRecordRepository;

import com.yourteam.library.entity.User;
import com.yourteam.library.service.UserService;

import library_api.dto.ApiResponse;
import library_api.dto.AdminUserDetailResponse;
import library_api.dto.UserResponse;
import library_api.dto.UpdateRoleLevelRequest;
import com.yourteam.library.repository.UserRepository;
import com.yourteam.library.service.PenaltyService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminUserController {

    private final UserService userService;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public AdminUserController() {
        this.userService = new UserService();
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.bookRepository = new BookRepository();
        this.userRepository = new UserRepository();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(
            @RequestParam(required = false, defaultValue = "") String keyword) {

        List<User> users = userService.searchUsers(keyword);
        List<UserResponse> responseList = new ArrayList<>();

        for (User user : users) {
            responseList.add(new UserResponse(
                    user.getStudentNo(),
                    user.getName(),
                    user.getRoleLevel(),
                    user.getStatus(),
                    "USER"
            ));
        }

        return new ApiResponse<>(true, responseList, "查詢使用者成功");
    }

    @PatchMapping("/{studentId}/suspend")
    public ApiResponse<Boolean> suspendUser(@PathVariable String studentId) {
        boolean success = userService.suspendUser(studentId);

        if (success) {
            return new ApiResponse<>(true, true, "已停權");
        }
        return new ApiResponse<>(false, null, "停權失敗或找不到使用者");
    }

    @PutMapping("/{studentId}/role-level")
    public ApiResponse<Boolean> updateRoleLevel(
            @PathVariable String studentId,
            @RequestBody UpdateRoleLevelRequest request) {
        User user = userRepository.findByStudentNo(studentId);
        if (user == null) {
            return new ApiResponse<>(false, null, "找不到使用者");
        }

        String roleLevel = request.getRoleLevel() == null ? "" : request.getRoleLevel().trim().toUpperCase();
        if (!"NORMAL".equals(roleLevel) && !"VIP".equals(roleLevel)) {
            return new ApiResponse<>(false, null, "會員等級只能是 NORMAL 或 VIP");
        }

        boolean success = userRepository.updateRoleLevelByStudentNo(studentId, roleLevel);
        if (!success) {
            return new ApiResponse<>(false, null, "會員等級更新失敗");
        }
        return new ApiResponse<>(true, true, "會員等級已更新為 " + roleLevel);
    }


    @GetMapping("/{studentId}/borrow-history")
    public ApiResponse<List<BorrowRecord>> getBorrowHistory(@PathVariable String studentId) {
        User user = userService.findByStudentNo(studentId);
        if (user == null) {
            return new ApiResponse<>(false, null, "找不到使用者");
        }
        List<BorrowRecord> records = borrowRecordRepository.findRecordsByUserId(user.getUserId());
        return new ApiResponse<>(true, records, "查詢借閱紀錄成功");
    }

    @GetMapping("/{studentId}/detail")
    public ApiResponse<AdminUserDetailResponse> getUserDetail(@PathVariable String studentId) {
        User user = userService.findByStudentNo(studentId);
        if (user == null) return new ApiResponse<>(false, null, "找不到使用者");

        List<BorrowRecord> records = borrowRecordRepository.findRecordsByUserId(user.getUserId());
        List<Map<String, Object>> recordMaps = new ArrayList<>();
        for (BorrowRecord r : records) {
            Book book = bookRepository.findByBookId(r.getBookId());
            LocalDateTime comparisonDate = r.getReturnDate() == null ? LocalDateTime.now() : r.getReturnDate();
            long overdueDays = PenaltyService.calculateOverdueDays(r.getDueDate(), comparisonDate);
            boolean overdue = overdueDays > 0;
            String status = r.getReturnDate() != null
                    ? "RETURNED"
                    : overdue ? "OVERDUE" : "BORROWED";

            Map<String, Object> m = new HashMap<>();
            m.put("recordId", r.getRecordId());
            m.put("bookId", r.getBookId());
            m.put("bookTitle", book == null ? "未知書籍" : book.getTitle());
            m.put("borrowDate", r.getBorrowDate());
            m.put("dueDate", r.getDueDate());
            m.put("returnDate", r.getReturnDate());
            m.put("status", status);
            m.put("overdueDays", overdueDays);
            m.put("fineAmount", PenaltyService.calculateFine(overdueDays));
            recordMaps.add(m);
        }

        AdminUserDetailResponse resp = new AdminUserDetailResponse(
                user.getStudentNo(),
                user.getName(),
                user.getRoleLevel(),
                user.getStatus(),
                userRepository.countFavoritesByUserId(user.getUserId()),
                userRepository.countReviewsByUserId(user.getUserId()),
                recordMaps
        );
        return new ApiResponse<>(true, resp, "查詢使用者詳情成功");
    }

    @PatchMapping("/{studentId}/activate")
    public ApiResponse<Boolean> activateUser(@PathVariable String studentId) {
        boolean success = userService.activateUser(studentId);

        if (success) {
            return new ApiResponse<>(true, true, "已復權");
        }
        return new ApiResponse<>(false, null, "復權失敗或找不到使用者");
    }
}
