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
  <h1>操作日志</h1>
  <div class="container">
    <el-table :data="logList" border style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="operator" label="操作人" width="120" align="center" />
      <el-table-column prop="operation" label="操作" width="300" align="center" show-overflow-tooltip />
      <el-table-column prop="params" label="参数" width="200" align="center" show-overflow-tooltip />
      <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center" />
      <el-table-column prop="ip" label="IP地址" width="150" align="center" />
      <el-table-column prop="createTime" label="操作时间" width="200" align="center" />
    </el-table>
  </div>
</template>

<style scoped>
.container { margin: 10px 0px; }
</style>