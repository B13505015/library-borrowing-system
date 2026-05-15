package library_api.dto;

public class ReservationInfoResponse {
    private int waitingCount;
    private Integer myQueuePosition;

    public ReservationInfoResponse() {}

    public ReservationInfoResponse(int waitingCount, Integer myQueuePosition) {
        this.waitingCount = waitingCount;
        this.myQueuePosition = myQueuePosition;
    }

    public int getWaitingCount() { return waitingCount; }
    public void setWaitingCount(int waitingCount) { this.waitingCount = waitingCount; }
    public Integer getMyQueuePosition() { return myQueuePosition; }
    public void setMyQueuePosition(Integer myQueuePosition) { this.myQueuePosition = myQueuePosition; }
}
