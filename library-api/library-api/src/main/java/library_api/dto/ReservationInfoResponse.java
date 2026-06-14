package library_api.dto;

public class ReservationInfoResponse {
    private int waitingCount;
    private Integer myQueuePosition;
    private boolean alreadyBorrowing;
    private boolean alreadyReserved;
    private String activeReservationStatus;
    private Integer reservationId;
    private boolean canBorrowNotified;

    public ReservationInfoResponse() {}

    public ReservationInfoResponse(int waitingCount, Integer myQueuePosition, boolean alreadyBorrowing, boolean alreadyReserved,
                                   String activeReservationStatus, Integer reservationId, boolean canBorrowNotified) {
        this.waitingCount = waitingCount;
        this.myQueuePosition = myQueuePosition;
        this.alreadyBorrowing = alreadyBorrowing;
        this.alreadyReserved = alreadyReserved;
        this.activeReservationStatus = activeReservationStatus;
        this.reservationId = reservationId;
        this.canBorrowNotified = canBorrowNotified;
    }

    public int getWaitingCount() { return waitingCount; }
    public void setWaitingCount(int waitingCount) { this.waitingCount = waitingCount; }
    public Integer getMyQueuePosition() { return myQueuePosition; }
    public void setMyQueuePosition(Integer myQueuePosition) { this.myQueuePosition = myQueuePosition; }
    public boolean isAlreadyBorrowing() { return alreadyBorrowing; }
    public void setAlreadyBorrowing(boolean alreadyBorrowing) { this.alreadyBorrowing = alreadyBorrowing; }
    public boolean isAlreadyReserved() { return alreadyReserved; }
    public void setAlreadyReserved(boolean alreadyReserved) { this.alreadyReserved = alreadyReserved; }
    public String getActiveReservationStatus() { return activeReservationStatus; }
    public void setActiveReservationStatus(String activeReservationStatus) { this.activeReservationStatus = activeReservationStatus; }
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public boolean isCanBorrowNotified() { return canBorrowNotified; }
    public void setCanBorrowNotified(boolean canBorrowNotified) { this.canBorrowNotified = canBorrowNotified; }
}
