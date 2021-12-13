package com.hwua.erhai.service.impl;

import com.hwua.erhai.entity.*;
import com.hwua.erhai.mapper.*;
import com.hwua.erhai.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CarService implements ICarService {
    @Autowired
    private CarMapper carMapper;

    @Autowired
    private CarMapper4Ext carMapper4Ext;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    RecordMapper4Ext recordMapper4Ext;
    @Override
    public long countCars(Map<String, Object> conditions) {
        return carMapper4Ext.countByConditions(conditions);
    }

    @Override
    public List<Car4Ext> queryCars(Map<String, Object> conditions) {
        return carMapper4Ext.selectByConditions(conditions);
    }

    @Override
    public Car queryCarById(Integer carId) {
        return carMapper.selectByPrimaryKey(carId);
    }

    @Override
    public Car addCar(Car car) {
        int counts = carMapper.insert(car);
        if (counts == 0) {
            return null;
        }
        return carMapper.selectByPrimaryKey(car.getId());
    }

    @Override
    public Car updateCar(Car car) {
        int counts = carMapper.updateByPrimaryKey(car);
        if (counts == 0) {
            return null;
        }
        return carMapper.selectByPrimaryKey(car.getId());
    }

    @Override
    public Car deleteCarById(Integer carId) {
        Car car = carMapper.selectByPrimaryKey(carId);
        if (car == null) {
            return null;
        }
        carMapper.deleteByPrimaryKey(carId);
        return car;
    }

    @Override
    public Brand getBrandByName(String brandName) {
        BrandExample example = new BrandExample();
        example.createCriteria().andNameEqualTo(brandName);
        List<Brand> brands = brandMapper.selectByExample(example);
        if (brands == null || brands.size() == 0) {
            throw new IllegalArgumentException(
                    String.format("找不到Brand, brandName[%s]", brandName));
        }
        if (brands.size() > 1) {
            throw new IllegalArgumentException(
                    String.format("找到多个Brand, brandName[%s]", brandName));
        }
        return brands.get(0);
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        CategoryExample example = new CategoryExample();
        example.createCriteria().andNameEqualTo(categoryName);
        List<Category> categories = categoryMapper.selectByExample(example);
        if (categories == null || categories.size() == 0) {
            throw new IllegalArgumentException(String.format(
                    "找不到Category, categoryName[%s]", categoryName));
        }
        if (categories.size() > 1) {
            throw new IllegalArgumentException(String.format(
                    "找到多个Category, categoryName[%s]", categoryName));
        }
        return categories.get(0);
    }
    @Override
    public Category queryCategoryById(int id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Brand queryBrand(String name) {
        BrandExample brandExample = new BrandExample();
        brandExample.createCriteria().andNameEqualTo(name);
        List<Brand> brands = brandMapper.selectByExample(brandExample);
        if (brands == null || brands.size() == 0){
            throw  new IllegalArgumentException(String.format("找不到Brand,brandName[%s]",name));
        }
        if (brands.size() >1 ){
            throw  new IllegalArgumentException(String.format("找到多个Brand,brandName[%s]",name));
        }
        return brands.get(0);
    }

    @Override
    public Brand queryBrandById(int id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public long countRecord4Ext(Map<String, Object> conditions) {
        return recordMapper4Ext.countByConditions(conditions);
    }

    @Override
    public List<Record4Ext> queryRecord4Ext(Map<String, Object> conditions) {
        return recordMapper4Ext.selectByConditions(conditions);
    }

    @Override
    public Record queryNotReturnRecord(Integer userId, Integer carId) {
        RecordExample recordExample = new RecordExample();
        recordExample.createCriteria().andUserIdEqualTo(userId).andCarIdEqualTo(carId).andReturnDateIsNull().andPaymentIsNull();
        List<Record> records = recordMapper.selectByExample(recordExample);
        if (records== null || records.size() == 0){
            return  null;
        }
        if (records.size() > 1){
            return null;
        }
        return records.get(0);
    }

    @Override
    public Record rentCar(Record record) {
        Record R = null;
        int r = recordMapper.insert(record);
        if (r > 0){
            Car car = carMapper.selectByPrimaryKey(record.getCarId());
            if (car != null && car.getStatus() == 0 && car.getUsable() == 0){
                car.setStatus(1);
                int row = carMapper.updateByPrimaryKey(car);
                if (row > 0){
                    R = recordMapper.selectByPrimaryKey(record.getId());
                    return R;
                }
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Record rentCar(Car car) {
        Record record = new Record();
        recordMapper.insert(record);
        car.setStatus(1);
        carMapper.updateByPrimaryKey(car);
        return record;
    }

    @Override
    public Category queryCategory(String name) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andNameEqualTo(name);
        List<Category> categories = categoryMapper.selectByExample(categoryExample);
        if (categories == null || categories.size() == 0){
            throw  new IllegalArgumentException(String.format("找不到category,categoryName[%s]",name));
        }
        if (categories.size() >1 ){
            throw  new IllegalArgumentException(String.format("找到多个category,categoryName[%s]",name));
        }
        return categories.get(0);
    }

    @Override
    @Transactional
    public Record returnCar(Record record) {
        Record R = null;
        int r = recordMapper.updateByPrimaryKey(record);
        if (r > 0){
            Car car = carMapper.selectByPrimaryKey(record.getCarId());
            if (car.getStatus() == 1){
                car.setStatus(0);
                int row = carMapper.updateByPrimaryKey(car);
                if (row > 0){
                    R = recordMapper.selectByPrimaryKey(record.getId());
                    return R;
                }
            }
        }
        return null;
    }
}
