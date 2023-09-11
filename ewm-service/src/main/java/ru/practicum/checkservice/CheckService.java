package ru.practicum.checkservice;

import ru.practicum.categories.model.Category;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public interface CheckService {

    User checkUser(Long userId);

    Category checkCategory(Long categoryId);

    Event checkEvent(Long eventId);

    Request checkRequest(Long requestId);

    Compilation checkCompilation(Long compId);

    LocalDateTime parseDate(String date);
}