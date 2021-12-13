package com.hwua.erhai.mapper;

import com.hwua.erhai.entity.Record4Ext;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper4Ext {

    List<Record4Ext> selectByConditions(Map<String,Object> conditions);

    long countByConditions(Map<String,Object> conditions);


}
