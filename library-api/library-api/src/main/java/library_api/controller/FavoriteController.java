package library_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.repository.FavoriteRepository;

import library_api.dto.ApiResponse;
import library_api.dto.FavoriteRequest;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:8080")
public class FavoriteController {
    private final FavoriteRepository favoriteRepository = new FavoriteRepository();

    @PostMapping("/add")
    public ApiResponse<Boolean> add(@RequestBody FavoriteRequest req) {
        boolean ok = favoriteRepository.addFavorite(req.getUserId(), req.getBookId());
        return ok ? new ApiResponse<>(true, true, "已加入收藏") : new ApiResponse<>(false, null, "加入收藏失敗");
    }

    @PostMapping("/remove")
    public ApiResponse<Boolean> remove(@RequestBody FavoriteRequest req) {
        boolean ok = favoriteRepository.removeFavorite(req.getUserId(), req.getBookId());
        return ok ? new ApiResponse<>(true, true, "已移除收藏") : new ApiResponse<>(false, null, "移除收藏失敗");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Integer>> list(@org.springframework.web.bind.annotation.PathVariable int userId) {
        return new ApiResponse<>(true, favoriteRepository.getFavoriteBookIds(userId), "查詢收藏成功");
    }
}
