package library_api.dto;

public class ReservationInfoResponse {
    private int waitingCount;
    private Integer myQueuePosition;
    private boolean alreadyBorrowing;
    private boolean alreadyReserved;

    public ReservationInfoResponse() {}

    public ReservationInfoResponse(int waitingCount, Integer myQueuePosition, boolean alreadyBorrowing, boolean alreadyReserved) {
        this.waitingCount = waitingCount;
        this.myQueuePosition = myQueuePosition;
        this.alreadyBorrowing = alreadyBorrowing;
        this.alreadyReserved = alreadyReserved;
    }

    public int getWaitingCount() { return waitingCount; }
    public void setWaitingCount(int waitingCount) { this.waitingCount = waitingCount; }
    public Integer getMyQueuePosition() { return myQueuePosition; }
    public void setMyQueuePosition(Integer myQueuePosition) { this.myQueuePosition = myQueuePosition; }
    public boolean isAlreadyBorrowing() { return alreadyBorrowing; }
    public void setAlreadyBorrowing(boolean alreadyBorrowing) { this.alreadyBorrowing = alreadyBorrowing; }
    public boolean isAlreadyReserved() { return alreadyReserved; }
    public void setAlreadyReserved(boolean alreadyReserved) { this.alreadyReserved = alreadyReserved; }
}
