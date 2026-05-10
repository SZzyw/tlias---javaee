package com.way_ne.controller;

import com.way_ne.pojo.PageResult;
import com.way_ne.pojo.Result;
import com.way_ne.pojo.Student;
import com.way_ne.pojo.StudentQueryParam;
import com.way_ne.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    /**
     * 查询所有学生信息
     */
    @GetMapping
    public Result GetStudent(StudentQueryParam studentQueryParam){
        log.info("查询所有学生信息");
        PageResult<Student> list=studentService.findAll(studentQueryParam);
        return Result.success(list);
    }

    /**
     * 删除学生信息
     */
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids){
        log.info("删除学生信息{}",ids);
        studentService.deleteById(ids);
        return Result.success();
    }

    /**
     * 新增学生
     */
    @PostMapping
    public Result addStudent(@RequestBody Student student){
        log.info("新增学生{}",student);
        studentService.addStudent(student);
        return Result.success();
    }
    /**
     * 根据id查询学生
     */
    @GetMapping("/{ids}")
    public Result getInfo(@PathVariable Integer ids){
        log.info("根据id查询学生{}",ids);
        Student student=studentService.getById(ids);
        return Result.success(student);
    }

    /**
     * 修改学生信息
     */
    @PutMapping
    public Result update(@RequestBody Student student){
        log.info("修改学生信息{}",student);
        studentService.updateStudent(student);
        return Result.success();
    }

    /**
     * 违纪处理
     */
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id,@PathVariable Short score){
        log.info("违纪处理{}",id);
        studentService.violation(id,score);
        return Result.success();
    }
}
