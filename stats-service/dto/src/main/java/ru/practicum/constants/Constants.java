package ru.practicum.constants;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final LocalDateTime START_HISTORY = LocalDateTime.of(1982, 10, 9, 0, 0);

    private Constants() {
    }
}
