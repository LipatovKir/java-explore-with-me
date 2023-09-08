package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.categories.model.Category;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.user.model.User;
import ru.practicum.enums.State;
import ru.practicum.enums.StateAction;
import ru.practicum.checkservice.CheckService;
import ru.practicum.enums.Status;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.constants.Constants.START_HISTORY;
import static ru.practicum.enums.State.PUBLISHED;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    public static final String USER_NOT_INITIATOR = "Пользователь %s не инициировал событие %s.";
    private final CheckService checkService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = checkService.checkUser(userId);
        Category category = checkService.checkCategory(newEventDto.getCategory());
        Location location = locationRepository.save(LocationMapper.makeDtoInLocation(newEventDto.getLocation()));
        Event event = EventMapper.makeDtoInEvent(newEventDto, category, location, user);
        eventRepository.save(event);
        return EventMapper.makeEventInFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsUserById(Long userId,
                                                 Integer from,
                                                 Integer size) {
        checkService.checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest);
        return EventMapper.makeEventShortDtoList(events);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        checkService.checkUser(userId);
        checkService.checkEvent(eventId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        return EventMapper.makeEventInFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventUserById(UpdateEventDto updateEventDto,
                                            Long userId,
                                            Long eventId) {
        User user = checkService.checkUser(userId);
        Event event = checkService.checkEvent(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format(USER_NOT_INITIATOR, userId, eventId));
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException(String.format("Пользователь %s не может обновить опубликованное событие %s.", userId, eventId));
        }
        Event eventNew = requestUpdateEvent(event, updateEventDto);
        return EventMapper.makeEventInFullDto(eventNew);
    }

    @Override
    public List<RequestDto> getRequestsForEventIdByUserId(Long userId, Long eventId) {
        User user = checkService.checkUser(userId);
        Event event = checkService.checkEvent(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format(USER_NOT_INITIATOR, userId, eventId));
        }
        List<Request> requests = requestRepository.findByEventId(eventId);
        return RequestMapper.makeRequestDtoList(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateDtoResult updateStatusRequestsForEventIdByUserId(EventRequestStatusUpdateDtoRequest requestDto,
                                                                                    Long userId,
                                                                                    Long eventId) {
        User user = checkService.checkUser(userId);
        Event event = checkService.checkEvent(eventId);
        EventRequestStatusUpdateDtoResult result = EventRequestStatusUpdateDtoResult.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(Collections.emptyList())
                .build();
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format(USER_NOT_INITIATOR, userId, eventId));
        }
        if (Boolean.TRUE.equals(!event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            return result;
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Превышен лимит участников.");
        }
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        long freeCount = event.getParticipantLimit() - event.getConfirmedRequests();
        List<Request> requestsList = requestRepository.findAllById(requestDto.getRequestIds());
        for (Request request : requestsList) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Запрос должен иметь статус PENDING");
            }
            if (requestDto.getStatus().equals(Status.CONFIRMED) && freeCount > 0) {
                request.setStatus(Status.CONFIRMED);
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
                confirmedRequests.add(request);
                freeCount--;
            } else {
                request.setStatus(Status.REJECTED);
                rejectedRequests.add(request);
            }
        }
        result.setConfirmedRequests(RequestMapper.makeRequestDtoList(confirmedRequests));
        result.setRejectedRequests(RequestMapper.makeRequestDtoList(rejectedRequests));
        eventRepository.save(event);
        requestRepository.saveAll(requestsList);
        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(UpdateEventDto eventUpdateDto, Long eventId) {
        Event event = checkService.checkEvent(eventId);
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Событие %s уже опубликовано. ", event.getTitle()));
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(State.PUBLISHED);
            } else {
                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Событие %s не может быть отменено", event.getTitle()));
                }
                event.setState(State.CANCELED);
            }
        }
        Event updateEvent = requestUpdateEvent(event, eventUpdateDto);
        return EventMapper.makeEventInFullDto(updateEvent);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<String> states,
                                               List<Long> categories,
                                               String rangeStart,
                                               String rangeEnd,
                                               Integer from,
                                               Integer size) {
        LocalDateTime startTime = checkService.parseDate(rangeStart);
        LocalDateTime endTime = checkService.parseDate(rangeEnd);
        List<State> stateNew = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                stateNew.add(State.getStateValue(state));
            }
        }
        if (startTime != null && endTime != null && (startTime.isAfter(endTime))) {
            throw new ValidationException("Start не может быть позже End");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsByAdminFromParam(users,
                stateNew,
                categories,
                startTime,
                endTime,
                pageRequest);
        return EventMapper.makeEventFullDtoList(events);
    }

    @Override
    public EventFullDto getEventById(Long eventId, String uri, String ip) {

        Event event = checkService.checkEvent(eventId);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException(Event.class, String.format("Событие %s не опубликовано", eventId));
        }
        recordHit(uri, ip);
        event.setViews(getEventViewsCountById(event.getId()));
        eventRepository.save(event);
        return EventMapper.makeEventInFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByPublic(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sort,
                                                 Integer from,
                                                 Integer size,
                                                 String uri,
                                                 String ip) {
        LocalDateTime startTime = checkService.parseDate(rangeStart);
        LocalDateTime endTime = checkService.parseDate(rangeEnd);
        if (startTime != null && endTime != null && (startTime.isAfter(endTime))) {
            throw new ValidationException("Start не может быть позже End");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsByPublicFromParam(text,
                categories,
                paid,
                startTime,
                endTime,
                onlyAvailable,
                sort,
                pageRequest);
        recordHit(uri, ip);
        for (Event event : events) {
            event.setViews(getEventViewsCountById(event.getId()));
            eventRepository.save(event);
        }
        return EventMapper.makeEventShortDtoList(events);
    }

    private Event requestUpdateEvent(Event event, UpdateEventDto updateEventDto) {
        Optional.ofNullable(updateEventDto.getAnnotation())
                .filter(annotation -> !annotation.isBlank())
                .ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventDto.getCategory())
                .map(checkService::checkCategory)
                .ifPresent(event::setCategory);
        Optional.ofNullable(updateEventDto.getDescription())
                .filter(description -> !description.isBlank())
                .ifPresent(event::setDescription);
        Optional.ofNullable(updateEventDto.getEventDate())
                .ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventDto.getLocation())
                .map(LocationMapper::makeDtoInLocation)
                .ifPresent(event::setLocation);
        Optional.ofNullable(updateEventDto.getPaid())
                .ifPresent(event::setPaid);
        Optional.ofNullable(updateEventDto.getParticipantLimit())
                .ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventDto.getRequestModeration())
                .ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEventDto.getStateAction())
                .ifPresent(stateAction -> {
                    if (stateAction == StateAction.PUBLISH_EVENT) {
                        event.setState(PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else if (stateAction == StateAction.REJECT_EVENT ||
                            stateAction == StateAction.CANCEL_REVIEW) {
                        event.setState(State.CANCELED);
                    } else if (stateAction == StateAction.SEND_TO_REVIEW) {
                        event.setState(State.PENDING);
                    }
                });
        if (StringUtils.isNotBlank(updateEventDto.getTitle())) {
            event.setTitle(updateEventDto.getTitle());
        }
        locationRepository.save(event.getLocation());
        return eventRepository.save(event);
    }

    private void recordHit(String uri, String ip) {
        HitDto hitDto = HitDto.builder()
                .app("ewm-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        client.addHit(hitDto);
    }

    private Long getEventViewsCountById(Long eventId) {
        String uri = "/events/" + eventId;
        ResponseEntity<Object> response = client.getStats(START_HISTORY,
                LocalDateTime.now(),
                uri,
                true);
        List<StatsDto> result = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        if (result.isEmpty()) {
            return 0L;
        } else {
            return result.get(0).getHits();
        }
    }
}