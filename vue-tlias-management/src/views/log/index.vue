<script setup>
import { ref, onMounted } from 'vue'
import { listApi } from '@/api/log'

const logList = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  const result = await listApi()
  if (result.code) {
    logList.value = result.data
  }
  loading.value = false
})
</script>

<template>
  <div class="page-shell">
    <section class="page-header">
      <div class="page-header-copy">
        <p class="page-eyebrow">Audit Trail</p>
        <h1 class="page-title">操作日志</h1>
        <p class="page-description">追踪后台关键操作与请求耗时，帮助排查行为路径与系统使用情况。</p>
      </div>
      <div class="page-actions">
        <span class="page-chip">共 {{ logList.length }} 条日志</span>
      </div>
    </section>

    <section class="page-table-card">
      <div class="table-panel-header">
        <div>
          <h3 class="page-section-title">日志列表</h3>
          <p class="page-section-subtitle">包含操作人、动作描述、参数、耗时和 IP 信息。</p>
        </div>
      </div>
      <el-table :data="logList" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="operator" label="操作人" width="120" align="center" />
        <el-table-column prop="operation" label="操作" width="300" align="center" show-overflow-tooltip />
        <el-table-column prop="params" label="参数" width="200" align="center" show-overflow-tooltip />
        <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center" />
        <el-table-column prop="ip" label="IP地址" width="150" align="center" />
        <el-table-column prop="createTime" label="操作时间" width="200" align="center" />
      </el-table>
    </section>
  </div>
</template>

<style scoped></style>
