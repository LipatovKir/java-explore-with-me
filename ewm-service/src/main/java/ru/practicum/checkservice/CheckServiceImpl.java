package ru.practicum.checkservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.practicum.Constant.FORMATTER;


@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    private <T> T checkEntity(Optional<T> entity, Class<T> entityClass, String entityId) {
        return entity.orElseThrow(() -> new NotFoundException(entityClass, entityId + " не найден!"));
    }

    @Override
    public User checkUser(Long userId) {
        return checkEntity(userRepository.findById(userId), User.class, "Пользователь " + userId);
    }

    @Override
    public Category checkCategory(Long categoryId) {
        return checkEntity(categoryRepository.findById(categoryId), Category.class, "Категория " + categoryId);
    }

    @Override
    public Event checkEvent(Long eventId) {
        return checkEntity(eventRepository.findById(eventId), Event.class, "Событие " + eventId);
    }

    @Override
    public Request checkRequest(Long requestId) {
        return checkEntity(requestRepository.findById(requestId), Request.class, "Запрос " + requestId);
    }

    @Override
    public Compilation checkCompilation(Long compId) {
        return checkEntity(compilationRepository.findById(compId), Compilation.class, "Подборка событий " + compId);
    }

    @Override
    public LocalDateTime parseDate(String date) {
        if (date != null) {
            return LocalDateTime.parse(date, FORMATTER);
        } else {
            return null;
        }
    }
}