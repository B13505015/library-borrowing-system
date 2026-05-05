package library_api.dto;

// 回傳給前端的書籍資料
public class BookResponse {

    private int id;
    private String title;
    private String publisher;
    private int publishYear;
    private String edition;
    private String format;
    private String source;
    private String note;
    private String status;

    public BookResponse() {
    }

    public BookResponse(int id, String title, String publisher, int publishYear,
                        String edition, String format, String source, String note, String status) {
        this.id = id;
        this.title = title;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.edition = edition;
        this.format = format;
        this.source = source;
        this.note = note;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}