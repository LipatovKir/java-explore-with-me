package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.checkservice.CheckService;
import ru.practicum.comments.dto.CommentFullDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CheckService checkService;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentFullDto addComment(Long userId, Long eventId, NewCommentDto commentNewDto) {
        User user = checkService.checkUser(userId);
        Event event = checkService.checkEvent(eventId);
        Comment comment = CommentMapper.makeComment(commentNewDto, user, event);
        comment = commentRepository.save(comment);
        return CommentMapper.makeCommentFullDto(comment);
    }

    @Override
    @Transactional
    public CommentFullDto updateComment(Long userId, Long commentId, NewCommentDto commentNewDto) {
        Comment comment = checkUserCreatedComment(userId, commentId);
        updateCommentMessageIfPresent(comment, commentNewDto);
        comment = saveComment(comment);
        return CommentMapper.makeCommentFullDto(comment);
    }

    private Comment checkUserCreatedComment(Long userId, Long commentId) {
        Comment comment = checkService.checkComment(commentId);
        if (!userId.equals(comment.getUser().getId())) {
            throw new ConflictException(String.format("Пользователь %s не создавал комментарий %s.", userId, commentId));
        }
        return comment;
    }

    private void updateCommentMessageIfPresent(Comment comment, NewCommentDto commentNewDto) {
        String newMessage = commentNewDto.getMessage();
        if (StringUtils.isNotBlank(newMessage)) {
            comment.setMessage(newMessage);
        }
    }

    private Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deletePrivateComment(Long userId, Long commentId) {
        Comment comment = checkService.checkComment(commentId);
        checkService.checkUser(userId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException(String.format("Пользователь %s не создавал комментарий  %s", userId, commentId));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public Page<CommentShortDto> getCommentsByUserId(String rangeStart, String rangeEnd, Long userId, Integer page, Integer size) {
        checkService.checkUser(userId);
        PageRequest pageRequest = PageRequest.of(page, size);
        LocalDateTime startTime = checkService.parseDate(rangeStart);
        LocalDateTime endTime = checkService.parseDate(rangeEnd);
        validateDateTime(startTime, endTime);
        Page<Comment> commentPage = commentRepository.getCommentsByUserId(userId, startTime, endTime, pageRequest);
        return commentPage.map(CommentMapper::makeCommentShortDto);
    }

    @Override
    public List<CommentFullDto> getComments(String rangeStart, String rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime startTime = checkService.parseDate(rangeStart);
        LocalDateTime endTime = checkService.parseDate(rangeEnd);
        validateDateTime(startTime, endTime);
        List<Comment> commentList = commentRepository.getComments(startTime, endTime, pageRequest);
        return CommentMapper.makeCommentFullDtoList(commentList);
    }

    @Override
    @Transactional
    public void deleteAdminComment(Long commentId) {
        checkService.checkComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentShortDto> getCommentsByEventId(String rangeStart, String rangeEnd, Long eventId, Integer from, Integer size) {
        checkService.checkEvent(eventId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime startTime = checkService.parseDate(rangeStart);
        LocalDateTime endTime = checkService.parseDate(rangeEnd);
        validateDateTime(startTime, endTime);
        List<Comment> commentList = commentRepository.getCommentsByEventId(eventId, startTime, endTime, pageRequest);
        return CommentMapper.makeCommentShortDtoList(commentList);
    }

    private void validateDateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Время START не может быть позже END. ");
            }
            if (endTime.isAfter(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now())) {
                throw new ValidationException("Дата не может быть раньше текущей. ");
            }
        }
    }
}