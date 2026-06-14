package library_api.dto;

public class MyReservationResponse {
    private int reservationId;
    private int bookId;
    private String title;
    private String status;
    private Integer queuePosition;
    private int queuePriority;
    private String createdAt;
    private String notifiedAt;
    private String expiresAt;
    private boolean canBorrowNotified;

    public MyReservationResponse(int reservationId, int bookId, String title, String status, Integer queuePosition, int queuePriority,
                                 String createdAt, String notifiedAt, String expiresAt, boolean canBorrowNotified) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.title = title;
        this.status = status;
        this.queuePosition = queuePosition;
        this.queuePriority = queuePriority;
        this.createdAt = createdAt;
        this.notifiedAt = notifiedAt;
        this.expiresAt = expiresAt;
        this.canBorrowNotified = canBorrowNotified;
    }

    public int getReservationId() { return reservationId; }
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public Integer getQueuePosition() { return queuePosition; }
    public int getQueuePriority() { return queuePriority; }
    public String getCreatedAt() { return createdAt; }
    public String getNotifiedAt() { return notifiedAt; }
    public String getExpiresAt() { return expiresAt; }
    public boolean isCanBorrowNotified() { return canBorrowNotified; }
}
