package library_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.service.StatisticsService;

import library_api.dto.ApiResponse;
import library_api.dto.SubjectBorrowStatResponse;

@RestController
@RequestMapping("/api/admin/statistics")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminStatisticsController {
    private final StatisticsService statisticsService = new StatisticsService();

    @GetMapping("/subjects")
    public ApiResponse<List<SubjectBorrowStatResponse>> getSubjectBorrowStatistics() {
        return new ApiResponse<>(
                true,
                statisticsService.getSubjectBorrowStatistics(),
                "查詢主題借閱統計成功"
        );
    }
}
