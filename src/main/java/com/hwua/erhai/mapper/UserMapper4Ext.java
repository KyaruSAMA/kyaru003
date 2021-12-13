package com.hwua.erhai.mapper;

import com.hwua.erhai.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper4Ext {
    List<User> selectByConditions(Map<String,Object> conditions);

    long countByConditions(Map<String,Object> conditions);
}
