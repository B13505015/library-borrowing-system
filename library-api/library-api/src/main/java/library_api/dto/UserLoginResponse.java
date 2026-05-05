package library_api.dto;

// 後端回傳給前端的登入成功資料
public class UserLoginResponse {

    private int userId;
    private String studentId;
    private String name;
    private String role;
    private String status;
    private String token;

    public UserLoginResponse() {
    }

    public UserLoginResponse(int userId, String studentId, String name, String role, String status, String token) {
        this.userId = userId;
        this.studentId = studentId;
        this.name = name;
        this.role = role;
        this.status = status;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}