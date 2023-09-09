package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.TimeValidationException;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Transactional
    @Override
    public void createHit(HitDto hitDto) {
        hitRepository.save(HitMapper.makeHitInDto(hitDto));
    }

    @Override
    public List<StatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start != null && end != null && (start.isAfter(end))) {
            throw new TimeValidationException("Время START не может позже времени END ");
        }
        if (uris.isEmpty()) {
            if (Boolean.TRUE.equals(unique)) {
                log.info("Получение статистики по ip: ");
                return hitRepository.findAllStatsByUniqIp(start, end);
            } else {
                log.info("Получение всей статистики: ");
                return hitRepository.findAllStats(start, end);
            }
        } else {
            if (Boolean.TRUE.equals(unique)) {
                log.info("Получение статистики по uri и ip: ");
                return hitRepository.findStatsByUrisByUniqIp(start, end, uris);
            } else {
                log.info("Получение статистики по uri: ");
                return hitRepository.findStatsByUris(start, end, uris);
            }
        }
    }
}