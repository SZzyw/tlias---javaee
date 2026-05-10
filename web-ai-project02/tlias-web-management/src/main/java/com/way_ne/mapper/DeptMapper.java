package com.way_ne.mapper;

import com.way_ne.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeptMapper {
    /**
     * 查询所有部门
     */
    List<Dept> findAll();

    /**
     * 根据id删除部门
     */
    void deleteById(Integer deptId);
    /**
     * 新增部门
     */
    void add(Dept dept);
    /**
     * 根据id查询部门
     */
    Dept getId(Integer id);
    /**
     * 修改部门
     */
    void update(Dept dept);
}
