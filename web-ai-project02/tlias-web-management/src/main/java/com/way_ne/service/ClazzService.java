package com.way_ne.service;

import com.way_ne.pojo.Clazz;
import com.way_ne.pojo.ClazzQueryParam;
import com.way_ne.pojo.PageResult;

import java.util.List;

public interface ClazzService {
    /**
     * 查询所有班级信息
     */
    PageResult<Clazz> findAll(ClazzQueryParam clazzQueryParam);

    /**
     * 根据id删除班级
     */
    void deleteById(Integer ids);

    /**
     * 新增班级
     */
    void addClazz(Clazz clazz);

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
