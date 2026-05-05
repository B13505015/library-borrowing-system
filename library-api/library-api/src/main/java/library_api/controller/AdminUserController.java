package library_api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.entity.User;
import com.yourteam.library.service.UserService;

import library_api.dto.ApiResponse;
import library_api.dto.UserResponse;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController() {
        this.userService = new UserService();
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

    @PatchMapping("/{studentId}/activate")
    public ApiResponse<Boolean> activateUser(@PathVariable String studentId) {
        boolean success = userService.activateUser(studentId);

        if (success) {
            return new ApiResponse<>(true, true, "已復權");
        }
        return new ApiResponse<>(false, null, "復權失敗或找不到使用者");
    }
}