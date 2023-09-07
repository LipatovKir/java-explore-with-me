package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.categories.dto.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CategoryDto> getCategories(@PositiveOrZero
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive
                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Список категорий, where: from = {}, size = {}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable("catId") Long categoryId) {
        log.info("Получение категории  id {}", categoryId);
        return categoryService.getCategoryById(categoryId);
    }
}