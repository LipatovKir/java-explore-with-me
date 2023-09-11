package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto addRequest(Long userId, Long eventId);

    List<RequestDto> getRequestsByUserId(Long userId);

    RequestDto cancelUserRequest(Long userId, Long requestId);
}