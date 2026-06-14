package library_api.dto;

public class AdminReviewResponse {
    private int reviewId;
    private int bookId;
    private String bookTitle;
    private int userId;
    private String studentId;
    private String userName;
    private int rating;
    private String content;
    private String createdAt;

    public AdminReviewResponse(int reviewId, int bookId, String bookTitle, int userId,
                               String studentId, String userName, int rating,
                               String content, String createdAt) {
        this.reviewId = reviewId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.studentId = studentId;
        this.userName = userName;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getReviewId() { return reviewId; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public int getUserId() { return userId; }
    public String getStudentId() { return studentId; }
    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}
