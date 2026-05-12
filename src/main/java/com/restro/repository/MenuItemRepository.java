package com.restro.repository;

import com.restro.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import com.restro.entity.MenuItem;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    List<MenuItem> findByCategory(Category category);
}
