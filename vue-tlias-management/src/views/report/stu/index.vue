<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getStudentDegreeDataApi, getStudentCountDataApi, getStudentEntryTrendApi, getViolationRankApi } from '@/api/report'
import * as echarts from 'echarts'

const degreeChartRef = ref(null)
const countChartRef = ref(null)
const trendChartRef = ref(null)
const violationChartRef = ref(null)
let degreeChartInstance = null
let countChartInstance = null
let trendChartInstance = null
let violationChartInstance = null

const initChart = (container, option) => {
  const chart = echarts.init(container)
  chart.setOption(option)
  return chart
}

const degreeOption = (data) => ({
  title: { text: '学历分布', left: 'center' },
  tooltip: {},
  xAxis: { type: 'category', data: data.map(d => d.name), axisLabel: { rotate: 0 } },
  yAxis: { type: 'value' },
  grid: { bottom: 60 },
  series: [{ type: 'bar', data: data.map(d => d.value), itemStyle: { color: '#E6A23C' } }]
})

const countOption = (data) => ({
  title: { text: '班级人数', left: 'center' },
  tooltip: {},
  xAxis: { type: 'category', data: data.clazzList, axisLabel: { rotate: 20 } },
  yAxis: { type: 'value' },
  grid: { bottom: 80 },
  series: [{ type: 'bar', data: data.dataList, itemStyle: { color: '#F56C6C' } }]
})

onMounted(async () => {
  const [degreeRes, countRes, trendRes, vioRes] = await Promise.all([
    getStudentDegreeDataApi(),
    getStudentCountDataApi(),
    getStudentEntryTrendApi(),
    getViolationRankApi()
  ])

  if (degreeRes.code) {
    await nextTick()
    degreeChartInstance = initChart(degreeChartRef.value, degreeOption(degreeRes.data))
  }

  if (countRes.code) {
    await nextTick()
    countChartInstance = initChart(countChartRef.value, countOption(countRes.data))
  }

  if (trendRes.code && trendRes.data.length > 0) {
    await nextTick()
    trendChartInstance = initChart(trendChartRef.value, {
      title: { text: '入学趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trendRes.data.map(d => d.month) },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: trendRes.data.map(d => d.value), smooth: true, areaStyle: {}, itemStyle: { color: '#67C23A' } }]
    })
  }

  if (vioRes.code && vioRes.data.length > 0) {
    await nextTick()
    violationChartInstance = initChart(violationChartRef.value, {
      title: { text: '违纪排行', left: 'center' },
      tooltip: {},
      xAxis: { type: 'category', data: vioRes.data.map(d => d.name), axisLabel: { rotate: 20 } },
      yAxis: { type: 'value' },
      grid: { bottom: 60 },
      series: [{ type: 'bar', data: vioRes.data.map(d => d.score), itemStyle: { color: '#F56C6C' } }]
    })
  }
})

const exportExcel = async () => {
  const token = localStorage.getItem('token')
  const res = await fetch('/api/report/exportStudent', { headers: { token } })
  const blob = await res.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = '学员统计报表.xlsx'
  a.click(); URL.revokeObjectURL(url)
}

onUnmounted(() => {
  degreeChartInstance?.dispose()
  countChartInstance?.dispose()
  trendChartInstance?.dispose()
  violationChartInstance?.dispose()
})
</script>

<template>
  <h1>学员信息统计
    <el-button type="success" size="small" style="float: right;" @click="exportExcel">
      <el-icon><Download /></el-icon> 导出Excel
    </el-button>
  </h1>
  <div style="display: flex; gap: 20px; margin-bottom: 20px;">
    <div ref="degreeChartRef" style="width: 50%; height: 400px;"></div>
    <div ref="countChartRef" style="width: 50%; height: 400px;"></div>
  </div>
  <div style="display: flex; gap: 20px; margin-bottom: 20px;">
    <div ref="trendChartRef" style="width: 50%; height: 350px;"></div>
    <div ref="violationChartRef" style="width: 50%; height: 350px;"></div>
  </div>
</template>
