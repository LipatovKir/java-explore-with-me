package ru.practicum.request.service;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class RequestMapper {

    public RequestDto makeRequestInDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public List<RequestDto> makeRequestDtoList(Iterable<Request> requests) {
        List<RequestDto> result = new ArrayList<>();
        for (Request request : requests) {
            result.add(makeRequestInDto(request));
        }
        return result;
    }
}