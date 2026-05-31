<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getDashboardApi, getEmpEntryTrendApi, getStudentEntryTrendApi, getViolationRankApi } from '@/api/report'
import * as echarts from 'echarts'

const dashboard = ref({})
const empTrend = ref([])
const stuTrend = ref([])
const violations = ref([])
const loading = ref(true)

const empChartRef = ref(null)
const stuChartRef = ref(null)
const violationChartRef = ref(null)
let empChartInstance = null
let stuChartInstance = null
let violationChartInstance = null

onMounted(async () => {
  loading.value = true
  const [dashRes, empRes, stuRes, vioRes] = await Promise.all([
    getDashboardApi(),
    getEmpEntryTrendApi(),
    getStudentEntryTrendApi(),
    getViolationRankApi()
  ])
  if (dashRes.code) dashboard.value = dashRes.data
  if (empRes.code) empTrend.value = empRes.data
  if (stuRes.code) stuTrend.value = stuRes.data
  if (vioRes.code) violations.value = vioRes.data
  loading.value = false

  await nextTick()
  initCharts()
})

onUnmounted(() => {
  empChartInstance?.dispose()
  stuChartInstance?.dispose()
  violationChartInstance?.dispose()
})

const initCharts = () => {
  if (empTrend.value.length > 0) {
    empChartInstance = echarts.init(empChartRef.value)
    empChartInstance.setOption({
      title: { text: '员工入职趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: empTrend.value.map(d => d.month) },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: empTrend.value.map(d => d.value), smooth: true, areaStyle: {}, itemStyle: { color: '#409EFF' } }]
    })
  }

  if (stuTrend.value.length > 0) {
    stuChartInstance = echarts.init(stuChartRef.value)
    stuChartInstance.setOption({
      title: { text: '学员入学趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: stuTrend.value.map(d => d.month) },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: stuTrend.value.map(d => d.value), smooth: true, areaStyle: {}, itemStyle: { color: '#67C23A' } }]
    })
  }

  if (violations.value.length > 0) {
    violationChartInstance = echarts.init(violationChartRef.value)
    violationChartInstance.setOption({
      title: { text: '学员违纪排行', left: 'center' },
      tooltip: {},
      xAxis: { type: 'category', data: violations.value.map(d => d.name), axisLabel: { rotate: 20 } },
      yAxis: { type: 'value' },
      grid: { bottom: 60 },
      series: [{ type: 'bar', data: violations.value.map(d => d.score), itemStyle: { color: '#F56C6C' } }]
    })
  }
}
</script>

<template>
  <div v-loading="loading">
    <h1>数据概览</h1>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409EFF;"><el-icon size="30"><Avatar /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.empCount || 0 }}</div>
              <div class="stat-label">员工总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67C23A;"><el-icon size="30"><UserFilled /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.stuCount || 0 }}</div>
              <div class="stat-label">学员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #E6A23C;"><el-icon size="30"><HomeFilled /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.clazzCount || 0 }}</div>
              <div class="stat-label">班级总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #F56C6C;"><el-icon size="30"><HelpFilled /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.deptCount || 0 }}</div>
              <div class="stat-label">部门总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #909399;"><el-icon size="30"><WarningFilled /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.totalViolation || 0 }}</div>
              <div class="stat-label">违纪总次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #E040FB;"><el-icon size="30"><Delete /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.totalViolationScore || 0 }}</div>
              <div class="stat-label">违纪总扣分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="empChartRef" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="stuChartRef" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 违纪排行 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="violationChartRef" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>违纪学员列表</template>
          <el-table :data="violations" border style="width: 100%" max-height="300" size="small">
            <el-table-column type="index" label="排名" width="60" align="center" />
            <el-table-column prop="name" label="姓名" width="80" align="center" />
            <el-table-column prop="clazzName" label="班级" width="160" align="center" />
            <el-table-column prop="count" label="次数" width="70" align="center" />
            <el-table-column prop="score" label="扣分" width="70" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>