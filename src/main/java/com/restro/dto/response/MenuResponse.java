package com.restro.dto.response;

import com.restro.entity.Category;
import com.restro.entity.FoodType;
import com.restro.entity.MenuStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class MenuResponse {

	private UUID menuId;
	private String itemName;
	private String description;
	private Double price;
	private Integer preparationTime;
	private FoodType foodType;
	private MenuStatus status;
	private UUID categoryId;
	private String categoryName;
	private String imageUrl;

	public UUID getMenuId() {
		return menuId;
	}

	public void setMenuId(UUID menuId) {
		this.menuId = menuId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(Integer preparationTime) {
		this.preparationTime = preparationTime;
	}

	public FoodType getFoodType() {
		return foodType;
	}

	public void setFoodType(FoodType foodType) {
		this.foodType = foodType;
	}

	public MenuStatus getStatus() {
		return status;
	}

	public void setStatus(MenuStatus status) {
		this.status = status;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
