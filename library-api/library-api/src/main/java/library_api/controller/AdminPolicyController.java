package library_api.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.repository.LoanPolicyRepository;

import library_api.dto.ApiResponse;
import library_api.dto.FinePolicyRequest;

@RestController
@RequestMapping("/api/admin/policies")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminPolicyController {
    private final LoanPolicyRepository repo = new LoanPolicyRepository();

    @GetMapping("/loan")
    public ApiResponse<Map<String, Object>> getPolicy(@RequestParam String roleLevel) {
        Map<String, Object> p = repo.getPolicy(roleLevel);
        if (p == null) return new ApiResponse<>(false, null, "找不到設定");
        return new ApiResponse<>(true, p, "查詢成功");
    }

    @PatchMapping("/fine")
    public ApiResponse<Boolean> updateFine(@RequestBody FinePolicyRequest req) {
        boolean ok = repo.updateFinePolicy(req.getRoleLevel(), req.getOverdueFinePerDay(), req.getFineGraceDays());
        return ok ? new ApiResponse<>(true, true, "罰金設定已更新") : new ApiResponse<>(false, null, "更新失敗");
    }
}
