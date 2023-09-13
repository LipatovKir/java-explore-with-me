package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.Constant.FORMATTER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class HitController {

    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void addHit(@Valid
                       @RequestBody HitDto hitDto) {
        log.info("Создан HIT {}:", hitDto.getApp());
        hitService.addHit(hitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(value = HttpStatus.OK)
    public List<StatsDto> getStats(@RequestParam("start") String start,
                                   @RequestParam("end") String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);
        log.info("Вывод статистики: ");
        return hitService.getStats(startTime, endTime, uris, unique);
    }
}