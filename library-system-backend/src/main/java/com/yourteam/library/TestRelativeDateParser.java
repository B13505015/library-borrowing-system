package com.yourteam.library;

import java.time.LocalDateTime;

import com.yourteam.library.util.RelativeDateParser;

public class TestRelativeDateParser {

    public static void main(String[] args) {
        // 測試幾個相對日期字串
        String test1 = "-45 days";
        String test2 = "-38 days";
        String test3 = "-42 days";

        // 轉換成真正的 LocalDateTime
        LocalDateTime date1 = RelativeDateParser.parseRelativeDate(test1);
        LocalDateTime date2 = RelativeDateParser.parseRelativeDate(test2);
        LocalDateTime date3 = RelativeDateParser.parseRelativeDate(test3);

        // 印出結果
        System.out.println(test1 + " -> " + date1);
        System.out.println(test2 + " -> " + date2);
        System.out.println(test3 + " -> " + date3);
    }
}