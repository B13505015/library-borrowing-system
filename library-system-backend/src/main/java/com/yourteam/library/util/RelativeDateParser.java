package com.yourteam.library.util;

import java.time.LocalDateTime;

public class RelativeDateParser {

    // 將像 "-45 days"、"7 days" 這種相對時間字串
    // 轉成實際的 LocalDateTime
    public static LocalDateTime parseRelativeDate(String relativeDateStr) {
        // 先去除前後空白
        String value = relativeDateStr.trim();

        // 以空白切開，例如 "-45 days" 會變成 ["-45", "days"]
        String[] parts = value.split("\\s+");

        // 基本格式檢查
        if (parts.length != 2) {
            throw new IllegalArgumentException("相對日期格式錯誤: " + relativeDateStr);
        }

        // 解析天數，例如 -45
        int days = Integer.parseInt(parts[0]);

        // 單位應該是 day 或 days
        String unit = parts[1].toLowerCase();

        if (!unit.equals("day") && !unit.equals("days")) {
            throw new IllegalArgumentException("目前只支援 day / days 單位: " + relativeDateStr);
        }

        // 以現在時間為基準，加上（或減去）天數
        return LocalDateTime.now().plusDays(days);
    }
}