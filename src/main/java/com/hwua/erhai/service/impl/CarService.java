package com.hwua.erhai.service.impl;

import com.hwua.erhai.mapper.CarMapper;
import com.hwua.erhai.service.ICarService;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService implements ICarService {
@Autowired
   private CarMapper carmapper;
    @Override
    public int countCars(List<QueryCondition> conditions) {
        return 0;
    }
}
