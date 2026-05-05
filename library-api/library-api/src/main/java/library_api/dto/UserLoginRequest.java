package library_api.dto;

// 前端送來的使用者登入資料
public class UserLoginRequest {

    private String studentId;
    private String password;

    public UserLoginRequest() {
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