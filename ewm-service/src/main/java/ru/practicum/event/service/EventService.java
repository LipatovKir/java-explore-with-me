package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsUserById(Long userId, Integer from, Integer size);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEventUserById(UpdateEventDto updateEventDto, Long userId, Long eventId);

    List<RequestDto> getRequestsForEventIdByUserId(Long userId, Long eventId);

    EventRequestStatusUpdateDtoResult updateStatusRequestsForEventIdByUserId(EventRequestStatusUpdateDtoRequest requestDto, Long userId, Long eventId);

    EventFullDto updateEventByAdmin(UpdateEventDto updateEventDto, Long eventId);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, String startTime, String endTime, Integer from, Integer size);

    EventFullDto getEventById(Long eventId, String uri, String ip);

    List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, String startTime, String endTime, Boolean onlyAvailable, String sort, Integer from, Integer size, String uri, String ip);
}