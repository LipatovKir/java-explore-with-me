package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;
import ru.practicum.event.model.Event;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;

import java.time.LocalDateTime;

import ru.practicum.checkservice.CheckService;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final CheckService checkService;

    @Override
    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        User user = checkService.checkUser(userId);
        Event event = checkService.checkEvent(eventId);
        if (event.getParticipantLimit() <= event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("Превышено количество запросов на событие %s ", event));
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(String.format("Пользователь id %s не может отправить запрос на участие в своем событии ", user.getId()));
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException(String.format("Вами уже подан запрос на событие %s", event.getTitle()));
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException(String.format("Событиие %s еще не опубликовано ", eventId));
        } else {
            Request request = Request.builder()
                    .requester(user)
                    .event(event)
                    .created(LocalDateTime.now())
                    .status(Status.PENDING)
                    .build();
            if (Boolean.TRUE.equals(!event.getRequestModeration()) || event.getParticipantLimit() == 0) {
                request.setStatus(Status.CONFIRMED);
                request = requestRepository.save(request);
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
                eventRepository.save(event);
                return RequestMapper.makeRequestInDto(request);
            }
            request = requestRepository.save(request);
            return RequestMapper.makeRequestInDto(request);
        }
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {
        checkService.checkUser(userId);
        List<Request> requestList = requestRepository.findByRequesterId(userId);
        return RequestMapper.makeRequestDtoList(requestList);
    }

    @Override
    @Transactional
    public RequestDto cancelUserRequest(Long userId, Long requestId) {
        checkService.checkUser(userId);
        Request request = checkService.checkRequest(requestId);
        request.setStatus(Status.CANCELED);
        return RequestMapper.makeRequestInDto(requestRepository.save(request));
    }
}