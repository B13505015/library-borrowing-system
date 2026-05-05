package library_api.dto;

public class AdminLoginRequest {
    private String username;
    private String studentId;
    private String password;

    public String getUsername() {
        return username != null && !username.isBlank() ? username : studentId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStudentId() {
        return studentId != null && !studentId.isBlank() ? studentId : username;
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
