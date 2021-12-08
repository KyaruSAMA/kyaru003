package com.hwua.erhai.mapper;

import com.hwua.erhai.entity.Car4Ext;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CarMapper4Ext {

    List<Car4Ext> selectByConditions(Map<String,Object> conditions);

    long countByConditions(Map<String,Object> conditions);

    List<Car4Ext> selectPartialByConditions(Map<String,Object> conditions);
}
