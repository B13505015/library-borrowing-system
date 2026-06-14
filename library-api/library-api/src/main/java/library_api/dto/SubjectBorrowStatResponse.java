package library_api.dto;

public class SubjectBorrowStatResponse {
    private String subject;
    private long borrowCount;
    private double percentage;

    public SubjectBorrowStatResponse(String subject, long borrowCount, double percentage) {
        this.subject = subject;
        this.borrowCount = borrowCount;
        this.percentage = percentage;
    }

    public String getSubject() {
        return subject;
    }

    public long getBorrowCount() {
        return borrowCount;
    }

    public double getPercentage() {
        return percentage;
    }
}
