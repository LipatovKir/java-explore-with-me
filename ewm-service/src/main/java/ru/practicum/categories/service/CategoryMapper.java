package ru.practicum.categories.service;

import lombok.experimental.UtilityClass;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CategoryMapper {

    public CategoryDto makeCategoryInDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category makeDtoInCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public List<CategoryDto> makeCategoryDtoList(Iterable<Category> categories) {
        List<CategoryDto> result = new ArrayList<>();
        for (Category category : categories) {
            result.add(makeCategoryInDto(category));
        }
        return result;
    }
}