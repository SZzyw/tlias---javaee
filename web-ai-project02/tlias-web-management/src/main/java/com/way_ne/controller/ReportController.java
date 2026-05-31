package com.way_ne.controller;

import com.way_ne.pojo.ClazzOption;
import com.way_ne.pojo.JobOption;
import com.way_ne.pojo.Result;
import com.way_ne.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/empJobData")
    public Result getEmpJobData(){
        log.info("统计员工信息");
        JobOption jobOption=reportService.countEmpJobData();
        return Result.success(jobOption);
    }

    @GetMapping("/empGenderData")
    public Result getEmpGenderData(){
        log.info("统计员工性别信息");
        List<Map<String,Object>> genderList=reportService.getEmpGenderData();
        return Result.success(genderList);
    }

    @GetMapping("/studentDegreeData")
    public Result getStudentDegreeData(){
        log.info("学院简历统计");
        List<Map<String,Object>> degreeList=reportService.getStudentDegreeData();
        return Result.success(degreeList);
    }

    @GetMapping("/studentCountData")
    public Result getStudentCountData(){
        log.info("班级人数统计");
        ClazzOption clazzOption=reportService.getStudentCountData();
        return Result.success(clazzOption);
    }

    @GetMapping("/dashboard")
    public Result getDashboard(){
        log.info("查询仪表盘数据");
        Map<String, Object> data = reportService.getDashboard();
        return Result.success(data);
    }

    @GetMapping("/empEntryTrend")
    public Result getEmpEntryTrend(){
        log.info("查询员工入职趋势");
        List<Map<String, Object>> list = reportService.getEmpEntryTrend();
        return Result.success(list);
    }

    @GetMapping("/studentEntryTrend")
    public Result getStudentEntryTrend(){
        log.info("查询学员入学趋势");
        List<Map<String, Object>> list = reportService.getStudentEntryTrend();
        return Result.success(list);
    }

    @GetMapping("/violationRank")
    public Result getViolationRank(){
        log.info("查询违纪排行");
        List<Map<String, Object>> list = reportService.getViolationRank();
        return Result.success(list);
    }

    @GetMapping("/exportEmp")
    public void exportEmp(HttpServletResponse response) throws Exception {
        log.info("导出员工数据Excel");

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("员工统计");

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Tlias教育管理系统 - 员工统计报表");

        String[] headers = {"统计项目", "分类", "数值"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        JobOption jobOption = reportService.countEmpJobData();
        List jobList = jobOption.getJobList();
        List dataList = jobOption.getDataList();
        int rowIdx = 2;
        for (int i = 0; i < jobList.size(); i++) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("职位分布");
            row.createCell(1).setCellValue(jobList.get(i).toString());
            row.createCell(2).setCellValue(Double.parseDouble(dataList.get(i).toString()));
        }

        List<Map<String, Object>> gender = reportService.getEmpGenderData();
        for (Map<String, Object> g : gender) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("性别分布");
            row.createCell(1).setCellValue(g.get("name").toString());
            row.createCell(2).setCellValue(Double.parseDouble(g.get("value").toString()));
        }

        String fileName = URLEncoder.encode("员工统计报表.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try (OutputStream os = response.getOutputStream()) {
            wb.write(os);
        }
        wb.close();
    }

    @GetMapping("/exportStudent")
    public void exportStudent(HttpServletResponse response) throws Exception {
        log.info("导出学员数据Excel");
        List<Map<String, Object>> degreeData = reportService.getStudentDegreeData();
        ClazzOption clazzOption = reportService.getStudentCountData();

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("学员统计");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("Tlias教育管理系统 - 学员统计报表");

        String[] headers = {"统计项目", "分类", "数值"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowIdx = 2;
        for (Map<String, Object> d : degreeData) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("学历分布");
            row.createCell(1).setCellValue(d.get("name").toString());
            row.createCell(2).setCellValue(Double.parseDouble(d.get("value").toString()));
        }

        List clazzList = clazzOption.getClazzList();
        List clazzDataList = clazzOption.getDataList();
        for (int i = 0; i < clazzList.size(); i++) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("班级人数");
            row.createCell(1).setCellValue(clazzList.get(i).toString());
            row.createCell(2).setCellValue(Double.parseDouble(clazzDataList.get(i).toString()));
        }

        String fileName = URLEncoder.encode("学员统计报表.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try (OutputStream os = response.getOutputStream()) {
            wb.write(os);
        }
        wb.close();
    }
}
