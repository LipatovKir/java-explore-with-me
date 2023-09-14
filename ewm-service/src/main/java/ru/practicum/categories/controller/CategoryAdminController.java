package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid
                                   @RequestBody CategoryDto categoryDto) {
        log.info("Добавлена категория {} ", categoryDto.getName());
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto updateCategory(@Valid
                                      @RequestBody CategoryDto categoryDto,
                                      @PathVariable("catId") Long categoryId) {
        log.info("Обновлена категория {} ", categoryDto.getName());
        return categoryService.updateCategory(categoryDto, categoryId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long categoryId) {
        log.info("Удалена категория {} ", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}