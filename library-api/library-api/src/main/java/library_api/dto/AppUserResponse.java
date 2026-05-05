package library_api.dto;

public class AppUserResponse {

    private int userId;
    private String studentId;
    private String name;
    private String level;
    private String status;
    private String role;

    public AppUserResponse() {
    }

    public AppUserResponse(int userId, String studentId, String name, String level, String status, String role) {
        this.userId = userId;
        this.studentId = studentId;
        this.name = name;
        this.level = level;
        this.status = status;
        this.role = role;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}