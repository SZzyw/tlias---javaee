package com.way_ne.controller;

import com.way_ne.pojo.ClazzOption;
import com.way_ne.pojo.JobOption;
import com.way_ne.pojo.Result;
import com.way_ne.service.EmpService;
import com.way_ne.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportService reportService;
    /**
     * 统计员工信息
     */
    @GetMapping("/empJobData")
    public Result getEmpJobData(){
        log.info("统计员工信息");
        JobOption jobOption=reportService.countEmpJobData();
        return Result.success(jobOption);
    }
    /**
     * 统计员工性别信息
     */
    @GetMapping("/empGenderData")
    public Result getEmpGenderData(){
        log.info("统计员工性别信息");
        List<Map<String,Object>> genderList=reportService.getEmpGenderData();
        return Result.success(genderList);
    }

    /**
     * 学院简历统计
     */
    @GetMapping("/studentDegreeData")
    public Result getStudentDegreeData(){
        log.info("学院简历统计");
        List<Map<String,Object>> degreeList=reportService.getStudentDegreeData();
        return Result.success(degreeList);
    }

    /**
     * 班级人数统计
     */
    @GetMapping("/studentCountData")
    public Result getStudentCountData(){
        log.info("班级人数统计");
        ClazzOption clazzOption=reportService.getStudentCountData();
        return Result.success(clazzOption);
    }
}
