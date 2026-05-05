package library_api.dto;

public class AdminLoginRequest {

    private String username;
    private String studentId;
    private String password;

    public AdminLoginRequest() {
    }

    // 管理員登入主要使用 username
    // 如果前端不小心送 studentId，也可以 fallback 使用 studentId
    public String getUsername() {
        if (username != null && !username.isBlank()) {
            return username;
        }
        return studentId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // 保留 studentId 是為了相容舊前端格式
    public String getStudentId() {
        if (studentId != null && !studentId.isBlank()) {
            return studentId;
        }
        return username;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}