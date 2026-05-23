<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getEmpJobDataApi, getEmpGenderDataApi } from '@/api/report'
import * as echarts from 'echarts'

const jobChartRef = ref(null)
const genderChartRef = ref(null)
let jobChartInstance = null
let genderChartInstance = null

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
})

onUnmounted(() => {
  jobChartInstance?.dispose()
  genderChartInstance?.dispose()
})
</script>

<template>
  <h1>员工信息统计</h1>
  <div style="display: flex; gap: 20px;">
    <div ref="jobChartRef" style="width: 50%; height: 400px;"></div>
    <div ref="genderChartRef" style="width: 50%; height: 400px;"></div>
  </div>
</template>
