package com.restro.repository;


import com.restro.entity.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, UUID> {

    Page<FoodCategory> findAll(Pageable pageable);

    List<FoodCategory> findByCategoryNameContainingIgnoreCase(String keyword);
}
