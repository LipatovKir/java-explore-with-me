package ru.practicum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constant {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final LocalDateTime START_OF_REPORT = LocalDateTime.of(1982, 9, 10, 0, 0);

    private Constant() {
    }
}