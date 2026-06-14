package library_api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.entity.User;
import com.yourteam.library.repository.UserRepository;
import com.yourteam.library.service.AuthService;

import library_api.dto.ApiResponse;
import library_api.dto.AppUserResponse;
import library_api.dto.AuthSessionResponse;
import library_api.dto.UserLoginRequest;

import com.yourteam.library.entity.Admin;
import com.yourteam.library.repository.AdminRepository;

import library_api.dto.AdminLoginRequest;

import com.yourteam.library.service.RegisterService;
import library_api.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RegisterService registerService;

    public AuthController() {
        this.authService = new AuthService();
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
        this.registerService = new RegisterService();
    }

    @GetMapping("/test")
    public Map<String, Object> testApi() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "後端 API 測試成功");
        result.put("port", 8081);
        return result;
    }

    @PostMapping("/user-login")
    public ApiResponse<AuthSessionResponse> userLogin(@RequestBody UserLoginRequest request) {

        System.out.println("studentId: " + request.getStudentId());
        System.out.println("password: " + request.getPassword());

        String loginResult = authService.loginUser(request.getStudentId(), request.getPassword());

        if (!"LOGIN_SUCCESS".equals(loginResult)) {
            String message;

            switch (loginResult) {
                case "USER_NOT_FOUND":
                    message = "學號不存在";
                    break;
                case "PASSWORD_INCORRECT":
                    message = "密碼錯誤";
                    break;
                case "USER_SUSPENDED":
                    message = "此帳號已被停權，請聯絡管理員";
                    break;
                default:
                    message = "登入失敗";
                    break;
            }

            return new ApiResponse<>(false, null, message);
        }

        User user = userRepository.findByStudentNo(request.getStudentId());

        if (user == null) {
            return new ApiResponse<>(false, null, "登入成功但查無使用者資料");
        }

        AppUserResponse appUser = new AppUserResponse(
                user.getUserId(),
                user.getStudentNo(),
                user.getName(),
                user.getRoleLevel(),
                user.getStatus(),
                "USER"
        );

        AuthSessionResponse session = new AuthSessionResponse(
                "real-user-token-" + user.getStudentNo(),
                appUser
        );

        return new ApiResponse<>(true, session, "使用者登入成功");
    }
    
    @PostMapping("/admin-login")
    public ApiResponse<AuthSessionResponse> adminLogin(@RequestBody AdminLoginRequest request) {

        System.out.println("admin username: " + request.getUsername());
        System.out.println("admin password: " + request.getPassword());

        String loginResult = authService.loginAdmin(request.getUsername(), request.getPassword());

        if (!"LOGIN_SUCCESS".equals(loginResult)) {
            String message;

            switch (loginResult) {
                case "ADMIN_NOT_FOUND":
                    message = "管理員帳號不存在";
                    break;
                case "PASSWORD_INCORRECT":
                    message = "密碼錯誤";
                    break;
                case "ADMIN_DISABLED":
                    message = "此管理員帳號已停用";
                    break;
                default:
                    message = "管理員登入失敗";
                    break;
            }

            return new ApiResponse<>(false, null, message);
        }

        Admin admin = adminRepository.findByUsername(request.getUsername());

        if (admin == null) {
            return new ApiResponse<>(false, null, "登入成功但查無管理員資料");
        }

        AppUserResponse appUser = new AppUserResponse(
                admin.getAdminId(),
                admin.getUsername(),
                admin.getName(),
                "管理員",
                admin.getStatus(),
                "ADMIN"
        );

        AuthSessionResponse session = new AuthSessionResponse(
                "real-admin-token-" + admin.getUsername(),
                appUser
        );

        return new ApiResponse<>(true, session, "管理員登入成功");
    }
    
    
    @PostMapping("/register")
    public ApiResponse<Boolean> register(@RequestBody RegisterRequest request) {

        System.out.println("register studentId: " + request.getStudentId());
        System.out.println("register name: " + request.getName());

        String result = registerService.registerUser(
                request.getStudentId(),
                request.getName(),
                request.getPassword(),
                request.getLevel(),
                request.getPaymentConfirmed()
        );

        switch (result) {
            case "REGISTER_SUCCESS":
                return new ApiResponse<>(true, true, "註冊成功");
            case "STUDENT_EXISTS":
                return new ApiResponse<>(false, null, "此學號已被註冊");
            case "INVALID_ROLE_LEVEL":
                return new ApiResponse<>(false, null, "會員等級只能是 NORMAL 或 VIP");
            case "VIP_PAYMENT_REQUIRED":
                return new ApiResponse<>(false, null, "請先完成 VIP 模擬付款確認");
            default:
                return new ApiResponse<>(false, null, "註冊失敗");
        }
    }
}
