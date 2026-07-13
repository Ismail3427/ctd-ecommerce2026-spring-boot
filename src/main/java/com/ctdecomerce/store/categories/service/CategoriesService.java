package com.ctdecomerce.store.categories.service;

import com.ctdecomerce.store.categories.repository.CategoriesRepo;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@AllArgsConstructor
@Service
public class CategoriesService {
    private final CategoriesRepo categoriesRepo;
}
