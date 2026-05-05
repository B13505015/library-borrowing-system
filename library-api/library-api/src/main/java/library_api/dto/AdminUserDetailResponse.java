package library_api.dto;

import java.util.List;
import java.util.Map;

public class AdminUserDetailResponse {
    private String studentId;
    private String name;
    private String level;
    private String status;
    private int favoriteCount;
    private int reviewCount;
    private List<Map<String, Object>> borrowRecords;

    public AdminUserDetailResponse(String studentId, String name, String level, String status,
                                   int favoriteCount, int reviewCount, List<Map<String, Object>> borrowRecords) {
        this.studentId = studentId;
        this.name = name;
        this.level = level;
        this.status = status;
        this.favoriteCount = favoriteCount;
        this.reviewCount = reviewCount;
        this.borrowRecords = borrowRecords;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getLevel() { return level; }
    public String getStatus() { return status; }
    public int getFavoriteCount() { return favoriteCount; }
    public int getReviewCount() { return reviewCount; }
    public List<Map<String, Object>> getBorrowRecords() { return borrowRecords; }
}
