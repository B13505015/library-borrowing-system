package library_api.dto;

import java.util.List;

public class EditBookRequest {

    private String title;
    private String authors;
    private String subjects;
    private List<String> isbns;
    private String publisher;
    private int publishYear;
    private String edition;
    private String format;
    private String source;
    private String note;

    public EditBookRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getSubjects() { return subjects; }
    public void setSubjects(String subjects) { this.subjects = subjects; }
    public List<String> getIsbns() { return isbns; }
    public void setIsbns(List<String> isbns) { this.isbns = isbns; }

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
}
