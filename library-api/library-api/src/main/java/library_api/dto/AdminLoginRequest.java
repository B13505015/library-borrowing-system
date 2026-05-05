package library_api.dto;

// 前端送來的管理員登入資料
public class AdminLoginRequest {

    private String username;
    private String password;

    public AdminLoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}