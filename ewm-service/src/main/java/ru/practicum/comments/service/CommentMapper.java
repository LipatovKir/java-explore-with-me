package ru.practicum.comments.service;

import lombok.experimental.UtilityClass;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class CommentMapper {

    public Comment makeComment(NewCommentDto commentNewDto, User user, Event event) {
        return Comment.builder()
                .user(user)
                .event(event)
                .message(commentNewDto.getMessage())
                .created(LocalDateTime.now())
                .build();
    }

    public CommentFullDto makeCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .user(UserMapper.makeUserInDto(comment.getUser()))
                .event(EventMapper.makeEventInFullDto(comment.getEvent()))
                .message(comment.getMessage())
                .created(comment.getCreated())
                .build();
    }

    public CommentShortDto makeCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .userName(comment.getUser().getName())
                .eventTitle(comment.getEvent().getTitle())
                .message(comment.getMessage())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentFullDto> makeCommentFullDtoList(Iterable<Comment> comments) {
        List<CommentFullDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(makeCommentFullDto(comment));
        }
        return result;
    }

    public List<CommentShortDto> makeCommentShortDtoList(Iterable<Comment> comments) {
        List<CommentShortDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(makeCommentShortDto(comment));
        }
        return result;
    }
}