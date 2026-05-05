package library_api.dto;

public class UserDashboardResponse {
    private int totalBooks;
    private int borrowedBooks;
    private int availableBooks;
    private int remainingQuota;

    public UserDashboardResponse(int totalBooks, int borrowedBooks, int availableBooks, int remainingQuota) {
        this.totalBooks = totalBooks;
        this.borrowedBooks = borrowedBooks;
        this.availableBooks = availableBooks;
        this.remainingQuota = remainingQuota;
    }

    public int getTotalBooks() { return totalBooks; }
    public int getBorrowedBooks() { return borrowedBooks; }
    public int getAvailableBooks() { return availableBooks; }
    public int getRemainingQuota() { return remainingQuota; }
}
