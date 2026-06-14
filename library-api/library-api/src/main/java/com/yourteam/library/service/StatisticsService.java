package com.yourteam.library.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.yourteam.library.repository.BorrowRecordRepository;

import library_api.dto.SubjectBorrowStatResponse;

public class StatisticsService {
    private final BorrowRecordRepository borrowRecordRepository;

    public StatisticsService() {
        this.borrowRecordRepository = new BorrowRecordRepository();
    }

    public List<SubjectBorrowStatResponse> getSubjectBorrowStatistics() {
        Map<String, Long> counts = new LinkedHashMap<>();
        Map<String, String> displayNames = new LinkedHashMap<>();

        for (String subjectsValue : borrowRecordRepository.findBorrowedBookSubjects()) {
            if (subjectsValue == null || subjectsValue.isBlank()) continue;
            Set<String> countedForRecord = new HashSet<>();
            for (String rawSubject : subjectsValue.split("[,，]")) {
                String subject = rawSubject.trim();
                if (subject.isEmpty()) continue;
                String normalized = subject.toLowerCase(Locale.ROOT);
                if (!countedForRecord.add(normalized)) continue;
                displayNames.putIfAbsent(normalized, subject);
                counts.merge(normalized, 1L, Long::sum);
            }
        }

        long totalSubjectBorrows = counts.values().stream().mapToLong(Long::longValue).sum();
        if (totalSubjectBorrows == 0) return List.of();

        List<Map.Entry<String, Long>> sortedCounts = new ArrayList<>(counts.entrySet());
        sortedCounts.sort((left, right) -> {
            int countComparison = Long.compare(right.getValue(), left.getValue());
            return countComparison != 0
                    ? countComparison
                    : displayNames.get(left.getKey()).compareTo(displayNames.get(right.getKey()));
        });

        List<SubjectBorrowStatResponse> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : sortedCounts) {
            result.add(new SubjectBorrowStatResponse(
                        displayNames.get(entry.getKey()),
                        entry.getValue(),
                        roundPercentage(entry.getValue(), totalSubjectBorrows)
            ));
        }
        return result;
    }

    private double roundPercentage(long count, long total) {
        return BigDecimal.valueOf(count * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
