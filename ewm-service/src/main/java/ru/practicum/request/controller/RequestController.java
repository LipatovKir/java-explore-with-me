package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId,
                                 @RequestParam Long eventId) {
        log.info("Пользователь id {} добавил запрос на событие id {}.", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestDto> getRequestsByUserId(@PathVariable Long userId) {
        log.info("Получение всех запросов пользователя id {}.", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    public RequestDto cancelUserRequest(@PathVariable Long userId,
                                        @PathVariable Long requestId) {
        log.info("Пользователь id {} удалил запрос id {}.", userId, requestId);
        return requestService.cancelUserRequest(userId, requestId);
    }
}