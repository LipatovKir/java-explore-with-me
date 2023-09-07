package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.EventRepository;
import ru.practicum.util.CheckService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CheckService checkService;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.makeDtoInCompilation(newCompilationDto);
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(newCompilationDto.getEvents()));
        }
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.makeCompilationInDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        checkService.checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = checkService.checkCompilation(compId);
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (updateCompilationRequest.getEvents() == null || updateCompilationRequest.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.makeCompilationInDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (Boolean.TRUE.equals(pinned)) {
            compilations = compilationRepository.findByPinned(true, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }
        return new ArrayList<>(CompilationMapper.makeCompilationInDtoSet(compilations));
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = checkService.checkCompilation(compId);
        return CompilationMapper.makeCompilationInDto(compilation);
    }
}