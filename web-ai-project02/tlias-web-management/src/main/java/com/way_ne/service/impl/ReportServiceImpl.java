package com.way_ne.service.impl;

import com.way_ne.mapper.EmpMapper;
import com.way_ne.mapper.StudentMapper;
import com.way_ne.pojo.ClazzOption;
import com.way_ne.pojo.JobOption;
import com.way_ne.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    EmpMapper empMapper;
    @Autowired
    StudentMapper studentMapper;

    @Override
    public JobOption countEmpJobData() {
        List<Map<String,Object>> list=empMapper.countEmpJobData();
        List<Object> jobList=list.stream().map(data->data.get("pos")).toList();
        List<Object> dataList=list.stream().map(data->data.get("num")).toList();
        return new JobOption(jobList, dataList);
    }

    @Override
    public List<Map<String, Object>> getEmpGenderData() {
        return empMapper.countEmpGenderData();
    }

    @Override
    public List<Map<String, Object>> getStudentDegreeData() {
        return studentMapper.countStudentDegreeData();
    }

    @Override
    public ClazzOption getStudentCountData() {
        List<Map<String,Object>> list=studentMapper.countStudentCountData();
        List clazzList=list.stream().map(data->data.get("name")).toList();
        List dataList=list.stream().map(data->data.get("value")).toList();
        return new ClazzOption(clazzList, dataList);
    }

    @Override
    public Map<String, Object> getDashboard() {
        return studentMapper.countDashboard();
    }

    @Override
    public List<Map<String, Object>> getEmpEntryTrend() {
        return empMapper.countEmpEntryTrend();
    }

    @Override
    public List<Map<String, Object>> getStudentEntryTrend() {
        return studentMapper.countStudentEntryTrend();
    }

    @Override
    public List<Map<String, Object>> getViolationRank() {
        return studentMapper.countViolationRank();
    }
}
