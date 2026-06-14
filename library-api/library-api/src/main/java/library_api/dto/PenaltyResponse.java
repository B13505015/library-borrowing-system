package library_api.dto;

public class PenaltyResponse {
    private Integer penaltyId;
    private int recordId;
    private int bookId;
    private String bookTitle;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private long overdueDays;
    private double amount;
    private String status;
    private boolean settled;
    private boolean payable;

    public PenaltyResponse(Integer penaltyId, int recordId, int bookId, String bookTitle,
                           String borrowDate, String dueDate, String returnDate,
                           long overdueDays, double amount, String status,
                           boolean settled, boolean payable) {
        this.penaltyId = penaltyId;
        this.recordId = recordId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.overdueDays = overdueDays;
        this.amount = amount;
        this.status = status;
        this.settled = settled;
        this.payable = payable;
    }

    public Integer getPenaltyId() { return penaltyId; }
    public int getRecordId() { return recordId; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getBorrowDate() { return borrowDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnDate() { return returnDate; }
    public long getOverdueDays() { return overdueDays; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public boolean isSettled() { return settled; }
    public boolean isPayable() { return payable; }
}
