package library_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourteam.library.service.ReturnService;

import library_api.dto.ApiResponse;
import library_api.dto.ReturnRequest;

@RestController
@RequestMapping("/api/return")
@CrossOrigin(origins = "http://localhost:8080")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController() {
        this.returnService = new ReturnService();
    }

    @PostMapping
    public ApiResponse<Boolean> returnBook(@RequestBody ReturnRequest request) {

        System.out.println("return recordId: " + request.getRecordId());

        String result = returnService.returnBook(request.getRecordId());

        switch (result) {
            case "RETURN_SUCCESS":
                return new ApiResponse<>(true, true, "還書成功");
            case "RECORD_NOT_FOUND":
                return new ApiResponse<>(false, null, "找不到借閱紀錄");
            case "ALREADY_RETURNED":
                return new ApiResponse<>(false, null, "此書已歸還");
            case "BOOK_NOT_FOUND":
                return new ApiResponse<>(false, null, "找不到書籍");
            default:
                if (result != null && result.startsWith("RETURN_SUCCESS|")) {
                    String[] parts = result.split("\\|");
                    boolean notified = false;
                    double fine = 0;
                    for (String part : parts) {
                        if (part.startsWith("NOTIFIED=")) {
                            notified = Boolean.parseBoolean(part.substring("NOTIFIED=".length()));
                        }
                        if (part.startsWith("FINE=")) {
                            fine = Double.parseDouble(part.substring("FINE=".length()));
                        }
                    }
                    String message = "還書成功";
                    if (fine > 0) {
                        message += "，已產生逾期罰款 NT$" + String.format("%.2f", fine);
                    }
                    if (notified) {
                        message += "，並已通知下一位預約者";
                    }
                    return new ApiResponse<>(true, true, message);
                }
                return new ApiResponse<>(false, null, "還書失敗");
        }
    }
}
