<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getEmpJobDataApi, getEmpGenderDataApi, getEmpEntryTrendApi } from '@/api/report'
import * as echarts from 'echarts'

const jobChartRef = ref(null)
const genderChartRef = ref(null)
const trendChartRef = ref(null)
let jobChartInstance = null
let genderChartInstance = null
let trendChartInstance = null

const initChart = (container, option) => {
  const chart = echarts.init(container)
  chart.setOption(option)
  return chart
}

const jobOption = (data) => ({
  title: { text: '职位分布', left: 'center' },
  tooltip: {},
  xAxis: { type: 'category', data: data.jobList },
  yAxis: { type: 'value' },
  series: [{ type: 'bar', data: data.dataList, itemStyle: { color: '#409EFF' } }]
})

const genderOption = (data) => ({
  title: { text: '性别分布', left: 'center' },
  tooltip: {},
  xAxis: { type: 'category', data: data.map(d => d.name) },
  yAxis: { type: 'value' },
  series: [{ type: 'bar', data: data.map(d => d.value), itemStyle: { color: '#67C23A' } }]
})

onMounted(async () => {
  const jobRes = await getEmpJobDataApi()
  if (jobRes.code) {
    await nextTick()
    jobChartInstance = initChart(jobChartRef.value, jobOption(jobRes.data))
  }

  const genderRes = await getEmpGenderDataApi()
  if (genderRes.code) {
    await nextTick()
    genderChartInstance = initChart(genderChartRef.value, genderOption(genderRes.data))
  }

  const trendRes = await getEmpEntryTrendApi()
  if (trendRes.code && trendRes.data.length > 0) {
    await nextTick()
    trendChartInstance = initChart(trendChartRef.value, {
      title: { text: '入职趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: trendRes.data.map(d => d.month) },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: trendRes.data.map(d => d.value), smooth: true, areaStyle: {}, itemStyle: { color: '#E6A23C' } }]
    })
  }
})

const exportExcel = async () => {
  const token = localStorage.getItem('token')
  const res = await fetch('/api/report/exportEmp', { headers: { token } })
  const blob = await res.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = '员工统计报表.xlsx'
  a.click(); URL.revokeObjectURL(url)
}

onUnmounted(() => {
  jobChartInstance?.dispose()
  genderChartInstance?.dispose()
  trendChartInstance?.dispose()
})
</script>

<template>
  <h1>员工信息统计
    <el-button type="success" size="small" style="float: right;" @click="exportExcel">
      <el-icon><Download /></el-icon> 导出Excel
    </el-button>
  </h1>
  <div style="display: flex; gap: 20px; margin-bottom: 20px;">
    <div ref="jobChartRef" style="width: 50%; height: 400px;"></div>
    <div ref="genderChartRef" style="width: 50%; height: 400px;"></div>
  </div>
  <div style="display: flex; gap: 20px;">
    <div ref="trendChartRef" style="width: 100%; height: 350px;"></div>
  </div>
</template>
