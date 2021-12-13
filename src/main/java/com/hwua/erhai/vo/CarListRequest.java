package com.hwua.erhai.vo;

public class CarListRequest {
    // 从客户端请求里，读取出与汽车查询条件相关的参数
    private String carId;
    private String carBrand;
    private String carCategory;
    private String priceOrder;

    // 从客户端请求里，读取出分页相关的参数。其中：
    // page代表查询的是第几个分页，从1开始；
    // pageSize代表的是每个分页的记录行数
    private String page;
    private String pageSize;

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(String carCategory) {
        this.carCategory = carCategory;
    }

    public String getPriceOrder() {
        return priceOrder;
    }

    public void setPriceOrder(String priceOrder) {
        this.priceOrder = priceOrder;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
}
