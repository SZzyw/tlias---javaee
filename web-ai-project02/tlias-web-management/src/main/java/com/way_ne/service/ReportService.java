package com.way_ne.service;

import com.way_ne.pojo.ClazzOption;
import com.way_ne.pojo.JobOption;

import java.util.List;
import java.util.Map;

public interface ReportService {

    /**
     * 统计员工信息
     * @return
     */
    JobOption countEmpJobData();

    /**
     * 统计员工性别信息
     * @return
     */
    List<Map<String,Object>> getEmpGenderData();

    /**
     * 统计员工学历信息
     * @return
     */
    List<Map<String, Object>> getStudentDegreeData();

    /**
     * 统计班级信息
     * @return
     */
    ClazzOption getStudentCountData();
}
