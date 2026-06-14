package library_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.entity.User;
import com.yourteam.library.repository.PenaltyRepository;
import com.yourteam.library.repository.UserRepository;

import library_api.dto.ApiResponse;
import library_api.dto.PayPenaltyRequest;
import library_api.dto.PenaltyResponse;

@RestController
@RequestMapping("/api/penalties")
@CrossOrigin(origins = "http://localhost:8080")
public class PenaltyController {
    private final PenaltyRepository penaltyRepository = new PenaltyRepository();
    private final UserRepository userRepository = new UserRepository();

    @GetMapping("/user/{studentId}")
    public ApiResponse<List<PenaltyResponse>> getUserPenalties(@PathVariable String studentId) {
        User user = userRepository.findByStudentNo(studentId);
        if (user == null) {
            return new ApiResponse<>(false, null, "找不到使用者");
        }
        return new ApiResponse<>(
                true,
                penaltyRepository.findUserPenaltySummaries(user.getUserId()),
                "查詢罰款成功"
        );
    }

    @PostMapping("/{penaltyId}/pay")
    public ApiResponse<Boolean> payPenalty(
            @PathVariable int penaltyId,
            @RequestBody PayPenaltyRequest request) {
        if (request == null || request.getStudentId() == null || request.getStudentId().isBlank()) {
            return new ApiResponse<>(false, null, "缺少使用者資料");
        }
        String result = penaltyRepository.payPenalty(penaltyId, request.getStudentId().trim());
        return switch (result) {
            case "PAID" -> new ApiResponse<>(true, true, "罰款已繳納");
            case "ALREADY_PAID" -> new ApiResponse<>(true, true, "罰款已繳納");
            case "WAIVED" -> new ApiResponse<>(false, null, "罰款已免除，無需繳納");
            case "NOT_SETTLED" -> new ApiResponse<>(false, null, "請先歸還書籍後再繳納罰款");
            case "NOT_FOUND" -> new ApiResponse<>(false, null, "找不到罰款紀錄");
            default -> new ApiResponse<>(false, null, "繳納罰款失敗");
        };
    }
}
