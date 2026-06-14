import request from "@/utils/request";

export const getEmpJobDataApi = () => request.get("/report/empJobData");

export const getEmpGenderDataApi = () => request.get("/report/empGenderData");

export const getStudentDegreeDataApi = () =>
  request.get("/report/studentDegreeData");

export const getStudentCountDataApi = () =>
  request.get("/report/studentCountData");

export const getDashboardApi = () => request.get("/report/dashboard");

export const getEmpEntryTrendApi = () => request.get("/report/empEntryTrend");

export const getStudentEntryTrendApi = () =>
  request.get("/report/studentEntryTrend");

export const getViolationRankApi = () => request.get("/report/violationRank");
