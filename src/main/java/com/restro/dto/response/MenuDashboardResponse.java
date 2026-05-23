package com.restro.dto.response;

public class MenuDashboardResponse {

    private long totalItems;
    private long vegItems;
    private long nonVegItems;

    private long availableItems;
    private long outOfStockItems;

    private long categoryCount;

    private double averagePrice;

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public long getVegItems() {
        return vegItems;
    }

    public void setVegItems(long vegItems) {
        this.vegItems = vegItems;
    }

    public long getNonVegItems() {
        return nonVegItems;
    }

    public void setNonVegItems(long nonVegItems) {
        this.nonVegItems = nonVegItems;
    }

    public long getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(long availableItems) {
        this.availableItems = availableItems;
    }

    public long getOutOfStockItems() {
        return outOfStockItems;
    }

    public void setOutOfStockItems(long outOfStockItems) {
        this.outOfStockItems = outOfStockItems;
    }

    public long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(long categoryCount) {
        this.categoryCount = categoryCount;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }
}
