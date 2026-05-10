package com.way_ne.controller;

import com.way_ne.pojo.Dept;
import com.way_ne.pojo.Result;
import com.way_ne.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class DeptController {
    @Autowired
    private DeptService deptService;
    /**
     * 查询所有部门数据
     */
   @GetMapping("/depts")//查询所有部门数据,只有GET请求才能访问
    public Result list() {
       log.info("查询全部部门数据");
        List<Dept> list = deptService.findAll();
        return Result.success(list);
    }
    /**
     * 删除部门
     *一旦声明了 @RequestParam，该参数在请求时必须传递，如果不传递将会报错。（默认 required 为 true）
     * 如果请求参数名与形参变量名相同，直接定义方法形参即可接收。（省略 @RequestParam）
     */
    @DeleteMapping("/depts")
    public Result delete(@RequestParam(value = "id") Integer deptId){//讲请求参数id绑定给形参deptId
        log.info("根据id删除部门{}",deptId);
        deptService.deleteById(deptId);
        return Result.success();
    }
    /**
     * 新增部门
     */
    @PostMapping("/depts")
    public Result add(@RequestBody Dept dept){//接收前端传递的 JSON 数据，并自动转换为 Java 对象
        log.info("新增部门{}",dept);
        deptService.add(dept);
        return Result.success();
    }
    /**
     * 根据id查询部门
     */
    @GetMapping("/depts/{id}")
    public Result getInfo(@PathVariable("id") Integer id){//讲网页的id绑定给形参id
        log.info("根据id查询部门{}",id);
        Dept dept=deptService.getById(id);
        return Result.success(dept);
    }
    /**
     * 修改部门
     */
    @PutMapping("/depts")
    public Result update(@RequestBody Dept dept){
        log.info("修改部门{}",dept);
        deptService.update(dept);
        return Result.success();
    }
}

