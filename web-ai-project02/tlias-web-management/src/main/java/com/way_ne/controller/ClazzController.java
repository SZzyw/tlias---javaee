package com.way_ne.controller;

import com.way_ne.pojo.Clazz;
import com.way_ne.pojo.ClazzQueryParam;
import com.way_ne.pojo.PageResult;
import com.way_ne.pojo.Result;
import com.way_ne.security.RequiresPermission;
import com.way_ne.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzController {
    @Autowired
    private ClazzService clazzService;
    /**
     * 查询所有班级信息
     */
    @RequiresPermission("clazz:view")
    @GetMapping
    public Result GetClazz(ClazzQueryParam clazzQueryParam){
        log.info("查询所有班级信息");
        PageResult<Clazz> list=clazzService.findAll(clazzQueryParam);
        return Result.success(list);
    }

    /**
     * 根据id删除班级
     */
    @RequiresPermission("clazz:edit")
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable Integer ids){
        log.info("根据id删除班级");
        clazzService.deleteById(ids);
        return Result.success();
    }
    /**
     * 新增班级
     */
    @RequiresPermission("clazz:edit")
    @PostMapping
    public Result addClazz(@RequestBody Clazz clazz){
        log.info("新增班级{}",clazz);
        clazzService.addClazz(clazz);
        return Result.success();
    }
    /**
     * 根据id查询班级
     */
    @RequiresPermission("clazz:view")
    @GetMapping("/{ids}")
    public Result getInfo(@PathVariable Integer ids){
        log.info("根据id查询班级{}",ids);
        Clazz clazz=clazzService.getById(ids);
        return Result.success(clazz);
    }
    /**
     * 修改班级
     */
    @RequiresPermission("clazz:edit")
    @PutMapping
    public Result updateClazz(@RequestBody Clazz clazz){
        log.info("修改班级{}",clazz);
        clazzService.updateClazz(clazz);
        return Result.success();
    }
    /**
     * 查询班级信息
     */
    @RequiresPermission({"clazz:view", "student:view"})
    @GetMapping("/list")
    public Result list(){
        log.info("查询班级信息");
        List<Clazz> list=clazzService.list();
        return Result.success(list);
    }
}
