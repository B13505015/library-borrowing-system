package library_api.dto;

public class UserResponse {

    private String studentId;
    private String name;
    private String level;
    private String status;
    private String role;

    public UserResponse() {
    }

    public UserResponse(String studentId, String name, String level, String status, String role) {
        this.studentId = studentId;
        this.name = name;
        this.level = level;
        this.status = status;
        this.role = role;
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