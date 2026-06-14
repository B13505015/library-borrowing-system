package library_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.repository.PenaltyRepository;

import library_api.dto.AdminPenaltyResponse;
import library_api.dto.ApiResponse;
import library_api.dto.UpdatePenaltyStatusRequest;

@RestController
@RequestMapping("/api/admin/penalties")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminPenaltyController {
    private final PenaltyRepository penaltyRepository = new PenaltyRepository();

    @GetMapping
    public ApiResponse<List<AdminPenaltyResponse>> getPenalties(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        return new ApiResponse<>(
                true,
                penaltyRepository.findAllPenalties(keyword, status),
                "查詢罰款成功"
        );
    }

    @PutMapping("/{penaltyId}/status")
    public ApiResponse<Boolean> updateStatus(
            @PathVariable int penaltyId,
            @RequestBody UpdatePenaltyStatusRequest request) {
        String targetStatus = request == null || request.getStatus() == null
                ? ""
                : request.getStatus().trim().toUpperCase();
        String result = penaltyRepository.adminUpdatePenaltyStatus(penaltyId, targetStatus);
        return switch (result) {
            case "UPDATED" -> new ApiResponse<>(true, true,
                    "PAID".equals(targetStatus) ? "已標記為已繳" : "罰款已免除");
            case "INVALID_STATUS" -> new ApiResponse<>(false, null, "狀態只能是 PAID 或 WAIVED");
            case "INVALID_TRANSITION" -> new ApiResponse<>(false, null, "此罰款狀態不可再變更");
            case "NOT_FOUND" -> new ApiResponse<>(false, null, "找不到罰款紀錄");
            default -> new ApiResponse<>(false, null, "更新罰款狀態失敗");
        };
    }
}
