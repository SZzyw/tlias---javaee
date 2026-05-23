<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getStudentDegreeDataApi, getStudentCountDataApi } from '@/api/report'
import * as echarts from 'echarts'

const degreeChartRef = ref(null)
const countChartRef = ref(null)
let degreeChartInstance = null
let countChartInstance = null

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
  const degreeRes = await getStudentDegreeDataApi()
  if (degreeRes.code) {
    await nextTick()
    degreeChartInstance = initChart(degreeChartRef.value, degreeOption(degreeRes.data))
  }

  const countRes = await getStudentCountDataApi()
  if (countRes.code) {
    await nextTick()
    countChartInstance = initChart(countChartRef.value, countOption(countRes.data))
  }
})

onUnmounted(() => {
  degreeChartInstance?.dispose()
  countChartInstance?.dispose()
})
</script>

<template>
  <h1>学员信息统计</h1>
  <div style="display: flex; gap: 20px;">
    <div ref="degreeChartRef" style="width: 50%; height: 400px;"></div>
    <div ref="countChartRef" style="width: 50%; height: 400px;"></div>
  </div>
</template>
