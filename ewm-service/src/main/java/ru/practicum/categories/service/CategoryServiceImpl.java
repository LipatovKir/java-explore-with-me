package ru.practicum.categories.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.util.CheckService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CheckService checkService;

    @Transactional
    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.makeDtoInCategory(categoryDto);
        categoryRepository.save(category);
        return CategoryMapper.makeCategoryInDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = checkService.checkCategory(categoryId);
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return CategoryMapper.makeCategoryInDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        checkService.checkCategory(categoryId);
        if (!eventRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException(String.format("Категория с id %s не может быть удалена ", categoryId));
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return CategoryMapper.makeCategoryDtoList(categoryRepository.findAll(pageRequest));
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.makeCategoryInDto(checkService.checkCategory(categoryId));
    }
}