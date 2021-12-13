package com.hwua.erhai.service;

import com.hwua.erhai.entity.*;

import java.util.List;
import java.util.Map;

public interface ICarService {
    long countCars(Map<String, Object> conditions);

    List<Car4Ext> queryCars(Map<String, Object> conditions);

    Car queryCarById(Integer carId);

    Car addCar(Car car);

    Car updateCar(Car car);

    Car deleteCarById(Integer carId);

    Brand getBrandByName(String brandName);

    Category getCategoryByName(String categoryName);

    Record rentCar(Car car);
    Category queryCategory(String name);

    Category queryCategoryById(int id);

    Brand queryBrand (String name);

    Brand queryBrandById (int id);

    long countRecord4Ext(Map<String,Object> conditions);

    List<Record4Ext> queryRecord4Ext(Map<String,Object> conditions);

    Record queryNotReturnRecord(Integer userId,Integer carId);

    Record rentCar(Record record);

    Record returnCar(Record record);
}

