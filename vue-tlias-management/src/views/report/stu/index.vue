<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import {
  getStudentDegreeDataApi,
  getStudentCountDataApi,
  getStudentEntryTrendApi,
  getViolationRankApi,
} from "@/api/report";
import * as echarts from "echarts";

const degreeChartRef = ref(null);
const countChartRef = ref(null);
const trendChartRef = ref(null);
const violationChartRef = ref(null);
let degreeChartInstance = null;
let countChartInstance = null;
let trendChartInstance = null;
let violationChartInstance = null;

const initChart = (container, option) => {
  const chart = echarts.init(container);
  chart.setOption(option);
  return chart;
};

const degreeOption = (data) => ({
  title: { text: "学历分布", left: "center" },
  tooltip: {},
  xAxis: {
    type: "category",
    data: data.map((d) => d.name),
    axisLabel: { rotate: 0 },
  },
  yAxis: { type: "value" },
  grid: { bottom: 60 },
  series: [
    {
      type: "bar",
      data: data.map((d) => d.value),
      itemStyle: { color: "#c49662" },
    },
  ],
});

const countOption = (data) => ({
  title: { text: "班级人数", left: "center" },
  tooltip: {},
  xAxis: { type: "category", data: data.clazzList, axisLabel: { rotate: 20 } },
  yAxis: { type: "value" },
  grid: { bottom: 80 },
  series: [
    { type: "bar", data: data.dataList, itemStyle: { color: "#8f623b" } },
  ],
});

onMounted(async () => {
  const [degreeRes, countRes, trendRes, vioRes] = await Promise.all([
    getStudentDegreeDataApi(),
    getStudentCountDataApi(),
    getStudentEntryTrendApi(),
    getViolationRankApi(),
  ]);

  if (degreeRes.code) {
    await nextTick();
    degreeChartInstance = initChart(
      degreeChartRef.value,
      degreeOption(degreeRes.data)
    );
  }

  if (countRes.code) {
    await nextTick();
    countChartInstance = initChart(
      countChartRef.value,
      countOption(countRes.data)
    );
  }

  if (trendRes.code && trendRes.data.length > 0) {
    await nextTick();
    trendChartInstance = initChart(trendChartRef.value, {
      title: { text: "入学趋势", left: "center" },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: trendRes.data.map((d) => d.month) },
      yAxis: { type: "value" },
      series: [
        {
          type: "line",
          data: trendRes.data.map((d) => d.value),
          smooth: true,
          areaStyle: {},
          itemStyle: { color: "#4f7f73" },
        },
      ],
    });
  }

  if (vioRes.code && vioRes.data.length > 0) {
    await nextTick();
    violationChartInstance = initChart(violationChartRef.value, {
      title: { text: "违纪排行", left: "center" },
      tooltip: {},
      xAxis: {
        type: "category",
        data: vioRes.data.map((d) => d.name),
        axisLabel: { rotate: 20 },
      },
      yAxis: { type: "value" },
      grid: { bottom: 60 },
      series: [
        {
          type: "bar",
          data: vioRes.data.map((d) => d.score),
          itemStyle: { color: "#c86d5f" },
        },
      ],
    });
  }

  window.addEventListener("resize", handleResize);
});

const exportExcel = async () => {
  const token = localStorage.getItem("token");
  const res = await fetch("/api/report/exportStudent", { headers: { token } });
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "学员统计报表.xlsx";
  a.click();
  URL.revokeObjectURL(url);
};

const handleResize = () => {
  degreeChartInstance?.resize();
  countChartInstance?.resize();
  trendChartInstance?.resize();
  violationChartInstance?.resize();
};

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  degreeChartInstance?.dispose();
  countChartInstance?.dispose();
  trendChartInstance?.dispose();
  violationChartInstance?.dispose();
});
</script>

<template>
  <div class="page-shell">
    <section class="page-header">
      <div class="page-header-copy">
        <p class="page-eyebrow">Student Analytics</p>
        <h1 class="page-title">学员信息统计</h1>
        <p class="page-description">
          从学历、班级规模、入学趋势与违纪排行四个维度理解学员结构与教学风险。
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
            <h3 class="page-section-title">学历分布</h3>
            <p class="page-section-subtitle">
              查看生源层次与学历结构，辅助课程与服务设计。
            </p>
          </div>
        </div>
        <div ref="degreeChartRef" class="chart-surface"></div>
      </div>

      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">班级人数</h3>
            <p class="page-section-subtitle">
              识别班级容量差异，优化教学资源投放。
            </p>
          </div>
        </div>
        <div ref="countChartRef" class="chart-surface"></div>
      </div>
    </section>

    <section class="chart-grid">
      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">入学趋势</h3>
            <p class="page-section-subtitle">观察阶段性招生和入学节奏。</p>
          </div>
        </div>
        <div ref="trendChartRef" class="chart-surface"></div>
      </div>

      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">违纪排行</h3>
            <p class="page-section-subtitle">
              及时发现重点跟进对象与管理风险。
            </p>
          </div>
        </div>
        <div ref="violationChartRef" class="chart-surface"></div>
      </div>
    </section>
  </div>
</template>
