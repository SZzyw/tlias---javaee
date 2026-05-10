package com.way_ne.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.way_ne.mapper.StudentMapper;
import com.way_ne.pojo.PageResult;
import com.way_ne.pojo.Student;
import com.way_ne.pojo.StudentQueryParam;
import com.way_ne.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl  implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    @Override
    public PageResult<Student> findAll(StudentQueryParam studentQueryParam) {
        PageHelper.startPage(studentQueryParam.getPage(),studentQueryParam.getPageSize());
        List<Student> list=studentMapper.findAll(studentQueryParam);
        Page< Student> p=(Page< Student>)list;
        return new PageResult<Student>(p.getTotal(),list);
    }

    @Override
    public void deleteById(List<Integer> ids) {
        studentMapper.deleteById(ids);
    }

    @Override
    public void addStudent(Student student) {
        student.setViolationCount((short) 0);
        student.setViolationScore((short) 0);
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.addStudent(student);
    }

    @Override
    public Student getById(Integer ids) {
        return studentMapper.getById(ids);
    }

    @Override
    public void updateStudent(Student student) {
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.updateStudent(student);
    }

    @Override
    public void violation(Integer id, Short score) {
        Student student=studentMapper.getById(id);
        student.setViolationCount((short)(student.getViolationCount()+1));
        student.setViolationScore((short)(student.getViolationScore()+score));
        studentMapper.updateStudent(student);
    }
}
