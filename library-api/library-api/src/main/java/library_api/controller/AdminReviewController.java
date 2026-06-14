package library_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.repository.ReviewRepository;

import library_api.dto.AdminReviewResponse;
import library_api.dto.ApiResponse;

@RestController
@RequestMapping("/api/admin/reviews")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminReviewController {
    private final ReviewRepository reviewRepository = new ReviewRepository();

    @GetMapping
    public ApiResponse<List<AdminReviewResponse>> getReviews(
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return new ApiResponse<>(
                true,
                reviewRepository.findAllReviews(keyword),
                "查詢書評成功"
        );
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Boolean> deleteReview(@PathVariable int reviewId) {
        boolean success = reviewRepository.deleteReview(reviewId);
        return success
                ? new ApiResponse<>(true, true, "書評已刪除")
                : new ApiResponse<>(false, null, "找不到書評或書評已刪除");
    }
}
