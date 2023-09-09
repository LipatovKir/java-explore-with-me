package ru.practicum.checkservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.practicum.Util.FORMATTER;


@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(User.class, "Пользователь " + userId + " не найден! ");
        } else {
            return user.get();
        }
    }

    @Override
    public Category checkCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new NotFoundException(Category.class, "Категория " + categoryId + " не найдена!");
        } else {
            return category.get();
        }
    }

    @Override
    public Event checkEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new NotFoundException(Event.class, "Событие  " + eventId + " не найдено!");
        } else {
            return event.get();
        }
    }

    @Override
    public Request checkRequest(Long requestId) {
        Optional<Request> request = requestRepository.findById(requestId);
        if (request.isEmpty()) {
            throw new NotFoundException(Request.class, "Запрос " + requestId + " не обнаружен! ");
        } else {
            return request.get();
        }
    }

    @Override
    public Compilation checkCompilation(Long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isEmpty()) {
            throw new NotFoundException(Compilation.class, "Подборка событий " + compId + " не найдена!");
        } else {
            return compilation.get();
        }
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