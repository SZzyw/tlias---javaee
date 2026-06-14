<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import {
  getEmpJobDataApi,
  getEmpGenderDataApi,
  getEmpEntryTrendApi,
} from "@/api/report";
import * as echarts from "echarts";

const jobChartRef = ref(null);
const genderChartRef = ref(null);
const trendChartRef = ref(null);
let jobChartInstance = null;
let genderChartInstance = null;
let trendChartInstance = null;

const initChart = (container, option) => {
  const chart = echarts.init(container);
  chart.setOption(option);
  return chart;
};

const jobOption = (data) => ({
  title: { text: "职位分布", left: "center" },
  tooltip: {},
  xAxis: { type: "category", data: data.jobList },
  yAxis: { type: "value" },
  series: [
    { type: "bar", data: data.dataList, itemStyle: { color: "#8f623b" } },
  ],
});

const genderOption = (data) => ({
  title: { text: "性别分布", left: "center" },
  tooltip: {},
  xAxis: { type: "category", data: data.map((d) => d.name) },
  yAxis: { type: "value" },
  series: [
    {
      type: "bar",
      data: data.map((d) => d.value),
      itemStyle: { color: "#4f7f73" },
    },
  ],
});

onMounted(async () => {
  const jobRes = await getEmpJobDataApi();
  if (jobRes.code) {
    await nextTick();
    jobChartInstance = initChart(jobChartRef.value, jobOption(jobRes.data));
  }

  const genderRes = await getEmpGenderDataApi();
  if (genderRes.code) {
    await nextTick();
    genderChartInstance = initChart(
      genderChartRef.value,
      genderOption(genderRes.data)
    );
  }

  const trendRes = await getEmpEntryTrendApi();
  if (trendRes.code && trendRes.data.length > 0) {
    await nextTick();
    trendChartInstance = initChart(trendChartRef.value, {
      title: { text: "入职趋势", left: "center" },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: trendRes.data.map((d) => d.month) },
      yAxis: { type: "value" },
      series: [
        {
          type: "line",
          data: trendRes.data.map((d) => d.value),
          smooth: true,
          areaStyle: {},
          itemStyle: { color: "#c49662" },
        },
      ],
    });
  }

  window.addEventListener("resize", handleResize);
});

const exportExcel = async () => {
  const token = localStorage.getItem("token");
  const res = await fetch("/api/report/exportEmp", { headers: { token } });
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "员工统计报表.xlsx";
  a.click();
  URL.revokeObjectURL(url);
};

const handleResize = () => {
  jobChartInstance?.resize();
  genderChartInstance?.resize();
  trendChartInstance?.resize();
};

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  jobChartInstance?.dispose();
  genderChartInstance?.dispose();
  trendChartInstance?.dispose();
});
</script>

<template>
  <div class="page-shell">
    <section class="page-header">
      <div class="page-header-copy">
        <p class="page-eyebrow">Employee Analytics</p>
        <h1 class="page-title">员工信息统计</h1>
        <p class="page-description">
          从职位、性别和入职趋势三个维度观察组织结构，辅助招聘、培训与团队配置。
        </p>
      </div>
      <div class="page-actions">
        <el-button type="success" @click="exportExcel">
          <el-icon><Download /></el-icon> 导出Excel
        </el-button>
      </div>
    </section>

    <section class="chart-grid">
      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">职位分布</h3>
            <p class="page-section-subtitle">
              查看组织岗位结构与当前配置重心。
            </p>
          </div>
        </div>
        <div ref="jobChartRef" class="chart-surface"></div>
      </div>

      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">性别分布</h3>
            <p class="page-section-subtitle">辅助理解团队构成与岗位画像。</p>
          </div>
        </div>
        <div ref="genderChartRef" class="chart-surface"></div>
      </div>
    </section>

    <section class="chart-grid-single">
      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">入职趋势</h3>
            <p class="page-section-subtitle">
              观察团队扩张与波动，为人力规划提供支撑。
            </p>
          </div>
        </div>
        <div ref="trendChartRef" class="chart-surface"></div>
      </div>
    </section>
  </div>
</template>
