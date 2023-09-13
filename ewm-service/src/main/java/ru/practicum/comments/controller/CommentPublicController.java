package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentShortDto> getCommentsByEventId(@PathVariable Long eventId,
                                                      @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                                      @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                                      @PositiveOrZero
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение всех комментариев к событию id {}, от {} и до {}.", eventId, rangeStart, rangeEnd);
        return commentService.getCommentsByEventId(rangeStart, rangeEnd, eventId, from, size);
    }
}