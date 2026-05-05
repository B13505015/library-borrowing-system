package library_api.dto;

// 前端送來的管理員登入資料
public class AdminLoginRequest {

    private String studentId;
    private String password;

    public AdminLoginRequest() {
    }

    public String getStudentId() {
        return studentId;
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