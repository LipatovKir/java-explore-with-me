package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto addEvent(@Valid
                                 @RequestBody NewEventDto newEventDto,
                                 @PathVariable Long userId) {
        log.info("Пользователь id {}, добавил событие {} ", userId, newEventDto.getAnnotation());
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable Long userId,
                                                    @PositiveOrZero
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Список событий пользователя Id {}. from = {}, size = {}", userId, from, size);
        return eventService.getEventsUserById(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        log.info("Получение события id {} пользователя id {} ", eventId, userId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEventByUserId(@RequestBody
                                            @Valid UpdateEventDto eventUpdateDto,
                                            @PathVariable Long userId,
                                            @PathVariable Long eventId) {
        log.info("Пользователь id {} обновил событие {} ", eventId, eventUpdateDto.getAnnotation());
        return eventService.updateEventUserById(eventUpdateDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestDto> getRequestsForEventIdByUserId(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.info("Получение всех запросов события id {} пользоваетля Id {}.", eventId, userId);
        return eventService.getRequestsForEventIdByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    public EventRequestStatusUpdateDtoResult updateStatusRequestsForEventIdByUserId(@PathVariable Long userId,
                                                                                    @PathVariable Long eventId,
                                                                                    @RequestBody EventRequestStatusUpdateDtoRequest requestDto) {
        log.info("Обновлен статус запроса события id {} пользователя id {}.", eventId, userId);
        return eventService.updateStatusRequestsForEventIdByUserId(requestDto, userId, eventId);
    }
}