package ru.practicum.compilation.service;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.EventMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public CompilationDto makeCompilationInDto(Compilation compilation) {
        List<EventShortDto> eventShortDtoList = EventMapper.returnEventShortDtoList(compilation.getEvents());
        Set<EventShortDto> eventShortDtoSet = new HashSet<>(eventShortDtoList);
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventShortDtoSet)
                .build();
    }

    public Compilation makeDtoInCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
    }

    public Set<CompilationDto> makeCompilationInDtoSet(Iterable<Compilation> compilations) {
        Set<CompilationDto> result = new HashSet<>();
        for (Compilation compilation : compilations) {
            result.add(makeCompilationInDto(compilation));
        }
        return result;
    }
}