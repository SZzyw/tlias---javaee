package com.way_ne.service;

import com.way_ne.pojo.PageResult;
import com.way_ne.pojo.Student;
import com.way_ne.pojo.StudentQueryParam;

import java.util.List;

public interface StudentService {
    /**
     * 查询所有学生信息
     */
    PageResult<Student> findAll(StudentQueryParam studentQueryParam);

    /**
     * 删除学生信息
     */
    void deleteById(List<Integer> ids);

    /**
     * 新增学生
     */
    void addStudent(Student student);

    /**
     * 根据id查询学生
     */
    Student getById(Integer ids);

    /**
     * 修改学生
     */
    void updateStudent(Student student);

    /**
     * 违纪处理
     */
    void violation(Integer id, Short score);
}
