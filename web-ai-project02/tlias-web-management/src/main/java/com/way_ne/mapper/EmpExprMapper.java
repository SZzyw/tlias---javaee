package com.way_ne.mapper;

import com.way_ne.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 员工工作经理
 */
@Mapper
public interface EmpExprMapper {
    /**
     * 批量插入员工工作经理数据
     */
    void insertBatch(List<EmpExpr> exprList);

    /**
     * 批量删除员工工作经理数据
     */
    void deleteByEmpIds(List<Integer> empIds);
}
