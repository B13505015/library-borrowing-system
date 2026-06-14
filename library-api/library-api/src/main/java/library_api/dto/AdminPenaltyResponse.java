package library_api.dto;

public class AdminPenaltyResponse {
    private int penaltyId;
    private int recordId;
    private int userId;
    private String studentId;
    private String userName;
    private int bookId;
    private String bookTitle;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private long overdueDays;
    private double amount;
    private String status;

    public AdminPenaltyResponse(int penaltyId, int recordId, int userId, String studentId,
                                String userName, int bookId, String bookTitle,
                                String borrowDate, String dueDate, String returnDate,
                                long overdueDays, double amount, String status) {
        this.penaltyId = penaltyId;
        this.recordId = recordId;
        this.userId = userId;
        this.studentId = studentId;
        this.userName = userName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.overdueDays = overdueDays;
        this.amount = amount;
        this.status = status;
    }

    public int getPenaltyId() { return penaltyId; }
    public int getRecordId() { return recordId; }
    public int getUserId() { return userId; }
    public String getStudentId() { return studentId; }
    public String getUserName() { return userName; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getBorrowDate() { return borrowDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnDate() { return returnDate; }
    public long getOverdueDays() { return overdueDays; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
}
