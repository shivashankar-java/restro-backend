package com.restro.service.impl;

import java.util.List;

import com.restro.entity.Category;
import org.springframework.stereotype.Service;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.MenuItem;
import com.restro.mapper.MenuMapper;
import com.restro.repository.MenuRepository;
import com.restro.service.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuRepository menuRepository, MenuMapper menuMapper) {
		super();
		this.menuRepository = menuRepository;
		this.menuMapper = menuMapper;
	}

	//  Add Menu
    @Override
    public MenuResponse addMenu(MenuRequest request) {
        MenuItem item = menuMapper.toEntity(request);
        return menuMapper.toResponse(menuRepository.save(item));
    }

    //  Get All Menu
    @Override
    public List<MenuResponse> getAllMenu() {
        return menuMapper.toResponseList(menuRepository.findAll());
    }

    //  Update Menu
    @Override
    public MenuResponse updateMenu(Long id, MenuRequest request) {

        MenuItem item = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());

        return menuMapper.toResponse(menuRepository.save(item));
    }

    //  Delete Menu
    @Override
    public void deleteMenu(Long id) {
    	menuRepository.deleteById(id);
    }

    @Override
    public List<MenuResponse> getMenuByCategory(Category category) {

        List<MenuItem> items = menuRepository.findByCategory(category);

        return menuMapper.toResponseList(items);
    }

}
