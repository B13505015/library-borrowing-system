package library_api.dto;

public class FavoriteRequest {
    private int userId;
    private int bookId;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
}
