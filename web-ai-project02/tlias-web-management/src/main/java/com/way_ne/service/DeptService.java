package com.way_ne.service;

import com.way_ne.pojo.Dept;

import java.util.List;

public interface DeptService {
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
    Dept getById(Integer id);
    /**
     * 修改部门
     */
    void update(Dept dept);
}
