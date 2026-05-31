package com.way_ne.mapper;

import com.way_ne.pojo.Student;
import com.way_ne.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    /**
     * 查询所有学生信息
     */
    public List<Student> findAll(StudentQueryParam studentQueryParam);

    /**
     * 删除学生信息
     */
    public void deleteById(List<Integer> ids);

    /**
     * 新增学生信息
     */
    public void addStudent(Student student);

    /**
     * 根据id查询学生信息
     */
    public Student getById(Integer ids);

    /**
     * 修改学生信息
     */
    void updateStudent(Student student);

    /**
     * 查询学生信息
     */
    @MapKey("name")
    List<Map<String, Object>> countStudentDegreeData();

    /**
     * 查询班级信息
     */
    List<Map<String, Object>> countStudentCountData();

    List<Map<String, Object>> countStudentEntryTrend();

    List<Map<String, Object>> countViolationRank();

    Map<String, Object> countDashboard();
}
