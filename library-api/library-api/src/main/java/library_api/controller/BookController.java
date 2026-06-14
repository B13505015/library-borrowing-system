package library_api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.entity.Book;
import com.yourteam.library.service.BookService;

import library_api.dto.ApiResponse;
import library_api.dto.BookResponse;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yourteam.library.repository.BookRepository;
import com.yourteam.library.repository.BookIsbnRepository;

import library_api.dto.AddBookRequest;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import library_api.dto.EditBookRequest;
import library_api.dto.PopularBookResponse;
import library_api.dto.ReservationInfoResponse;
import com.yourteam.library.repository.ReservationRepository;
import com.yourteam.library.repository.BorrowRecordRepository;
import library_api.dto.MyReservationResponse;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:8080")
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookIsbnRepository bookIsbnRepository;

    public BookController() {
        this.bookService = new BookService();
        this.bookRepository = new BookRepository();
        this.reservationRepository = new ReservationRepository();
        this.borrowRecordRepository = new BorrowRecordRepository();
        this.bookIsbnRepository = new BookIsbnRepository();
    }

    // 查詢全部書籍
    @GetMapping
    public ApiResponse<List<BookResponse>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookResponse> responseList = convertToBookResponseList(books);

        return new ApiResponse<>(true, responseList, "查詢全部書籍成功");
    }

    // 關鍵字搜尋書籍
    @GetMapping("/search")
    public ApiResponse<List<BookResponse>> searchBooks(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        List<BookResponse> responseList = convertToBookResponseList(books);

        return new ApiResponse<>(true, responseList, "搜尋書籍成功");
    }

    @GetMapping("/{bookId}")
    public ApiResponse<BookResponse> getBookDetail(@PathVariable int bookId) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return new ApiResponse<>(false, null, "找不到書籍");
        }
        return new ApiResponse<>(true, convertToBookResponse(book), "查詢書籍詳情成功");
    }


    @GetMapping("/popular")
    public ApiResponse<List<PopularBookResponse>> getPopularBooks(
            @RequestParam(defaultValue = "borrow") String sortBy,
            @RequestParam(defaultValue = "5") int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 20));
        List<PopularBookResponse> list = bookRepository.findPopularBooks(sortBy, safeLimit);
        return new ApiResponse<>(true, list, "查詢熱門書籍成功");
    }



    @GetMapping("/{bookId}/reservation-info")
    public ApiResponse<ReservationInfoResponse> getReservationInfo(
            @PathVariable int bookId,
            @RequestParam(required = false) Integer userId) {
        reservationRepository.repairInvalidNotifiedReservations(bookId);
        int waitingCount = reservationRepository.countWaitingReservations(bookId);
        Integer myQueuePosition = userId == null ? null : reservationRepository.findUserQueuePosition(userId, bookId);
        boolean alreadyBorrowing = userId != null && borrowRecordRepository.hasActiveBorrowByUserAndBook(userId, bookId);
        String activeReservationStatus = userId == null ? null : reservationRepository.findActiveReservationStatus(userId, bookId);
        Integer reservationId = userId == null ? null : reservationRepository.findActiveReservationId(userId, bookId);
        boolean alreadyReserved = activeReservationStatus != null;
        Book book = bookRepository.findByBookId(bookId);
        boolean canBorrowNotified = "NOTIFIED".equals(activeReservationStatus)
                && book != null
                && "AVAILABLE".equals(book.getStatus())
                && !borrowRecordRepository.hasActiveBorrowByBook(bookId);
        ReservationInfoResponse response = new ReservationInfoResponse(
                waitingCount, myQueuePosition, alreadyBorrowing, alreadyReserved, activeReservationStatus, reservationId,
                canBorrowNotified);
        return new ApiResponse<>(true, response, "查詢預約資訊成功");
    }


    @GetMapping("/reservation-notifications")
    public ApiResponse<List<String>> getReservationNotifications(@RequestParam int userId) {
        List<String> messages = reservationRepository.findNotifiedReservationMessages(userId);
        return new ApiResponse<>(true, messages, "查詢預約通知成功");
    }


    @GetMapping("/my-reservations")
    public ApiResponse<List<MyReservationResponse>> getMyReservations(@RequestParam int userId) {
        List<MyReservationResponse> list = reservationRepository.findMyActiveReservations(userId);
        return new ApiResponse<>(true, list, "查詢我的預約成功");
    }
    // 把 Book entity 轉成 BookResponse DTO
    private List<BookResponse> convertToBookResponseList(List<Book> books) {
        List<BookResponse> responseList = new ArrayList<>();

        for (Book book : books) {
            responseList.add(convertToBookResponse(book));
        }

        return responseList;
    }

    private BookResponse convertToBookResponse(Book book) {
        return new BookResponse(
                book.getBookId(),
                book.getTitle(),
                book.getAuthors(),
                book.getSubjects(),
                bookIsbnRepository.findIsbnsByBookId(book.getBookId()),
                book.getPublisher(),
                book.getPublishYear(),
                book.getEdition(),
                book.getFormat(),
                book.getSource(),
                book.getNote(),
                book.getStatus()
        );
    }
    
    @PostMapping
    public ApiResponse<Boolean> addBook(@RequestBody AddBookRequest request) {

        System.out.println("add book title: " + request.getTitle());

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthors(request.getAuthors());
        book.setSubjects(request.getSubjects());
        book.setPublisher(request.getPublisher());
        book.setPublishYear(request.getPublishYear());
        book.setEdition(request.getEdition());
        book.setFormat(request.getFormat());
        book.setSource(request.getSource());
        book.setNote(request.getNote());
        book.setStatus("AVAILABLE");

        int newBookId = bookRepository.insertBook(book, LocalDateTime.now());

        if (newBookId > 0) {
            if (!bookIsbnRepository.replaceIsbns(newBookId, request.getIsbns())) {
                return new ApiResponse<>(false, null, "新增書籍成功，但 ISBN 儲存失敗");
            }
            return new ApiResponse<>(true, true, "新增書籍成功");
        }

        return new ApiResponse<>(false, null, "新增書籍失敗");
    }
    
    
    @PutMapping("/{bookId}")
    public ApiResponse<Boolean> editBook(@PathVariable int bookId, @RequestBody EditBookRequest request) {

        Book existingBook = bookService.getBookById(bookId);

        if (existingBook == null) {
            return new ApiResponse<>(false, null, "找不到書籍");
        }

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthors(request.getAuthors());
        existingBook.setSubjects(request.getSubjects());
        existingBook.setPublisher(request.getPublisher());
        existingBook.setPublishYear(request.getPublishYear());
        existingBook.setEdition(request.getEdition());
        existingBook.setFormat(request.getFormat());
        existingBook.setSource(request.getSource());
        existingBook.setNote(request.getNote());

        boolean success = bookRepository.updateBook(existingBook);

        if (success) {
            if (!bookIsbnRepository.replaceIsbns(bookId, request.getIsbns())) {
                return new ApiResponse<>(false, null, "書籍資料已更新，但 ISBN 更新失敗");
            }
            return new ApiResponse<>(true, true, "更新書籍成功");
        }

        return new ApiResponse<>(false, null, "更新書籍失敗");
    }
    
    
    @DeleteMapping("/{bookId}")
    public ApiResponse<Boolean> removeBook(@PathVariable int bookId) {

        Book existingBook = bookService.getBookById(bookId);

        if (existingBook == null) {
            return new ApiResponse<>(false, null, "找不到書籍");
        }

        boolean success = bookRepository.removeBook(bookId);

        if (success) {
            return new ApiResponse<>(true, true, "下架書籍成功");
        }

        return new ApiResponse<>(false, null, "下架書籍失敗");
    }
}
