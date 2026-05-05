package library_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.repository.ReviewRepository;

import library_api.dto.ApiResponse;
import library_api.dto.ReviewRequest;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:8080")
public class ReviewController {
    private final ReviewRepository reviewRepository = new ReviewRepository();

    @PostMapping
    public ApiResponse<Boolean> addReview(@RequestBody ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            return new ApiResponse<>(false, null, "評分需介於 1~5");
        }
        boolean success = reviewRepository.addReview(request.getUserId(), request.getBookId(), request.getRating(), request.getComment());
        return success
                ? new ApiResponse<>(true, true, "評論新增成功")
                : new ApiResponse<>(false, null, "評論新增失敗");
    }

    @GetMapping("/book/{bookId}")
    public ApiResponse<List<String>> getBookReviews(@PathVariable int bookId) {
        return new ApiResponse<>(true, reviewRepository.findReviewsByBookId(bookId), "查詢評論成功");
    }
}
