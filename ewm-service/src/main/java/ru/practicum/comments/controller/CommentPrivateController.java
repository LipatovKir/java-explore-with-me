package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentFullDto addComment(@Valid
                                     @RequestBody NewCommentDto commentNewDto,
                                     @PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("Пользователь id {} добавил новый комментарий к событию {} ", userId, eventId);
        return commentService.addComment(userId, eventId, commentNewDto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentFullDto updateComment(@Valid
                                        @RequestBody NewCommentDto commentNewDto,
                                        @PathVariable Long userId,
                                        @PathVariable Long commentId) {
        log.info("Пользователь id {} обновил комментарий {} ", userId, commentId);
        return commentService.updateComment(userId, commentId, commentNewDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("Пользователь id {} удалил комментарий {} ", userId, commentId);
        commentService.deletePrivateComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Page<CommentShortDto> getCommentsByUserId(@PathVariable Long userId,
                                                     @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                                     @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                                     @PositiveOrZero
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение всех комментариев пользователя id {}, от {} и до {}.", userId, rangeStart, rangeEnd);
        return commentService.getCommentsByUserId(rangeStart, rangeEnd, userId, from, size);
    }
}