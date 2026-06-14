package com.yourteam.library.service;

import java.time.Duration;
import java.time.LocalDateTime;

public final class PenaltyService {
    public static final double FINE_PER_DAY = 5.0;

    private PenaltyService() {
    }

    public static long calculateOverdueDays(LocalDateTime dueDate, LocalDateTime comparisonDate) {
        if (dueDate == null || comparisonDate == null || !comparisonDate.isAfter(dueDate)) {
            return 0;
        }
        long overdueSeconds = Duration.between(dueDate, comparisonDate).getSeconds();
        return Math.max(1, (overdueSeconds + 86_399) / 86_400);
    }

    public static double calculateFine(long overdueDays) {
        return overdueDays > 0 ? overdueDays * FINE_PER_DAY : 0;
    }
}
