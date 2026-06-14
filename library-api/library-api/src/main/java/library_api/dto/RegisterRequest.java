package library_api.dto;

public class RegisterRequest {

    private String studentId;
    private String name;
    private String password;
    private String level;
    private Boolean paymentConfirmed;

    public RegisterRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getPaymentConfirmed() {
        return paymentConfirmed;
    }

    public void setPaymentConfirmed(Boolean paymentConfirmed) {
        this.paymentConfirmed = paymentConfirmed;
    }
}
