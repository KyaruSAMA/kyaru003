package com.hwua.erhai.mapper;

import com.hwua.erhai.entity.Car;
import com.hwua.erhai.entity.CarExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CarMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    long countByExample(CarExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int deleteByExample(CarExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int insert(Car record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int insertSelective(Car record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    List<Car> selectByExample(CarExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    Car selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") Car record, @Param("example") CarExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") Car record, @Param("example") CarExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Car record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_car
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Car record);
}