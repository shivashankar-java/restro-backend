package com.restro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restro.entity.MenuItem;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {
	
}
