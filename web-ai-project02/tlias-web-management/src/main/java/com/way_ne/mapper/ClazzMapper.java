package com.way_ne.mapper;

import com.way_ne.pojo.Clazz;
import com.way_ne.pojo.ClazzQueryParam;
import com.way_ne.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzMapper {
    /**
     * 查询所有班级信息
     */
    public List<Clazz> findAll(ClazzQueryParam clazzQueryParam);

    /**
     * 根据id删除班级
     */
    public void deleteById(Integer ids);

    /**
     * 新增班级
     */
    public void addClazz(Clazz clazz);

    /**
     * 根据id查询班级
     */
    Clazz getById(Integer ids);

    /**
     * 修改班级
     */
    void updateClazz(Clazz clazz);

    /**
     * 查询所有班级信息
     */
    List<Clazz> list();
}
