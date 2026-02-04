package com.gestion.eventos.api.service;

import com.gestion.eventos.api.domain.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    List<Category> findAll();
    Category save(Category category);
    Category update(Long id, Category category);
    Category findById(Long id);
    void deleteById(Long id);
}
