package library_api.dto;

public class PopularBookResponse {
    private int bookId;
    private String title;
    private int borrowCount;
    private double avgRating;
    private int reviewCount;

    public PopularBookResponse(int bookId, String title, int borrowCount, double avgRating, int reviewCount) {
        this.bookId = bookId;
        this.title = title;
        this.borrowCount = borrowCount;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public int getBorrowCount() { return borrowCount; }
    public double getAvgRating() { return avgRating; }
    public int getReviewCount() { return reviewCount; }
}
