package ru.practicum.comments.service;

import org.springframework.data.domain.Page;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentFullDto addComment(Long userId, Long eventId, NewCommentDto commentNewDto);

    CommentFullDto updateComment(Long userId, Long commentId, NewCommentDto commentNewDto);

    void deletePrivateComment(Long userId, Long commentId);

    Page<CommentShortDto> getCommentsByUserId(String rangeStart, String rangeEnd, Long userId, Integer from, Integer size);

    List<CommentFullDto> getComments(String rangeStart, String rangeEnd, Integer from, Integer size);

    void  deleteAdminComment(Long commentId);

    List<CommentShortDto> getCommentsByEventId(String rangeStart, String rangeEnd, Long eventId, Integer from, Integer size);
}