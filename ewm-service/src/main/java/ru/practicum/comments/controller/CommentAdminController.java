package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.info("Удален комментарий {} ", commentId);
        commentService.deleteAdminComment(commentId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentFullDto> getComments(@RequestParam(required = false, name = "rangeStart") String rangeStart,
                                            @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                            @PositiveOrZero
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение комментариев от {} до {}.", rangeStart, rangeEnd);
        return commentService.getComments(rangeStart, rangeEnd, from, size);
    }
}