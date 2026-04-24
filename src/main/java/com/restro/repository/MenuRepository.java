package com.restro.repository;

import com.restro.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import com.restro.entity.MenuItem;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategory(Category category);
}
