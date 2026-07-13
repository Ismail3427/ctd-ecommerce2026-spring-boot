package com.ctdecomerce.store.categories.repository;

import com.ctdecomerce.store.categories.model.CategoriesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriesRepo extends JpaRepository<CategoriesModel, UUID> {
}
