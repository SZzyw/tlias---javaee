<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from "vue";
import {
  Avatar,
  Delete,
  HelpFilled,
  HomeFilled,
  UserFilled,
  WarningFilled,
} from "@element-plus/icons-vue";
import {
  getDashboardApi,
  getEmpEntryTrendApi,
  getStudentEntryTrendApi,
  getViolationRankApi,
} from "@/api/report";
import * as echarts from "echarts";

const dashboard = ref({});
const empTrend = ref([]);
const stuTrend = ref([]);
const violations = ref([]);
const loading = ref(true);

const empChartRef = ref(null);
const stuChartRef = ref(null);
const violationChartRef = ref(null);
let empChartInstance = null;
let stuChartInstance = null;
let violationChartInstance = null;

const statCards = computed(() => [
  {
    label: "员工总数",
    value: dashboard.value.empCount || 0,
    icon: Avatar,
    theme: "copper",
    note: "组织成员总览",
  },
  {
    label: "学员总数",
    value: dashboard.value.stuCount || 0,
    icon: UserFilled,
    theme: "forest",
    note: "当前学员规模",
  },
  {
    label: "班级总数",
    value: dashboard.value.clazzCount || 0,
    icon: HomeFilled,
    theme: "amber",
    note: "班级资源分布",
  },
  {
    label: "部门总数",
    value: dashboard.value.deptCount || 0,
    icon: HelpFilled,
    theme: "rose",
    note: "组织结构节点",
  },
  {
    label: "违纪总次数",
    value: dashboard.value.totalViolation || 0,
    icon: WarningFilled,
    theme: "slate",
    note: "纪律事件累计",
  },
  {
    label: "违纪总扣分",
    value: dashboard.value.totalViolationScore || 0,
    icon: Delete,
    theme: "plum",
    note: "行为扣分统计",
  },
]);

const topViolation = computed(() => violations.value[0] || null);

const initCharts = () => {
  if (empTrend.value.length > 0) {
    empChartInstance = echarts.init(empChartRef.value);
    empChartInstance.setOption({
      title: { text: "员工入职趋势", left: "center" },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: empTrend.value.map((d) => d.month) },
      yAxis: { type: "value" },
      series: [
        {
          type: "line",
          data: empTrend.value.map((d) => d.value),
          smooth: true,
          areaStyle: {},
          itemStyle: { color: "#8f623b" },
        },
      ],
    });
  }

  if (stuTrend.value.length > 0) {
    stuChartInstance = echarts.init(stuChartRef.value);
    stuChartInstance.setOption({
      title: { text: "学员入学趋势", left: "center" },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: stuTrend.value.map((d) => d.month) },
      yAxis: { type: "value" },
      series: [
        {
          type: "line",
          data: stuTrend.value.map((d) => d.value),
          smooth: true,
          areaStyle: {},
          itemStyle: { color: "#4f7f73" },
        },
      ],
    });
  }

  if (violations.value.length > 0) {
    violationChartInstance = echarts.init(violationChartRef.value);
    violationChartInstance.setOption({
      title: { text: "学员违纪排行", left: "center" },
      tooltip: {},
      xAxis: {
        type: "category",
        data: violations.value.map((d) => d.name),
        axisLabel: { rotate: 20 },
      },
      yAxis: { type: "value" },
      grid: { bottom: 60 },
      series: [
        {
          type: "bar",
          data: violations.value.map((d) => d.score),
          itemStyle: { color: "#c86d5f" },
        },
      ],
    });
  }
};

const handleResize = () => {
  empChartInstance?.resize();
  stuChartInstance?.resize();
  violationChartInstance?.resize();
};

onMounted(async () => {
  loading.value = true;
  const [dashRes, empRes, stuRes, vioRes] = await Promise.all([
    getDashboardApi(),
    getEmpEntryTrendApi(),
    getStudentEntryTrendApi(),
    getViolationRankApi(),
  ]);
  if (dashRes.code) dashboard.value = dashRes.data;
  if (empRes.code) empTrend.value = empRes.data;
  if (stuRes.code) stuTrend.value = stuRes.data;
  if (vioRes.code) violations.value = vioRes.data;
  loading.value = false;

  await nextTick();
  initCharts();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  empChartInstance?.dispose();
  stuChartInstance?.dispose();
  violationChartInstance?.dispose();
});
</script>

<template>
  <div class="page-shell dashboard-page" v-loading="loading">
    <section class="page-header dashboard-hero">
      <div class="page-header-copy">
        <p class="page-eyebrow">Data Control Center</p>
        <h1 class="page-title">数据概览</h1>
        <p class="page-description">
          在一个视图中查看员工、学员、班级与纪律状态，让日常管理与运营判断基于同一份实时数据。
        </p>
      </div>
      <div class="page-actions dashboard-actions">
        <span class="page-chip">员工趋势 {{ empTrend.length }} 个月</span>
        <span class="page-chip">学员趋势 {{ stuTrend.length }} 个月</span>
        <span class="page-chip" v-if="topViolation"
          >重点关注 {{ topViolation.name }}</span
        >
      </div>
    </section>

    <section class="stats-grid">
      <article
        v-for="card in statCards"
        :key="card.label"
        class="stat-panel"
        :class="`theme-${card.theme}`"
      >
        <div class="stat-icon">
          <el-icon size="28"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">{{ card.label }}</p>
          <h3 class="stat-value">{{ card.value }}</h3>
          <p class="stat-note">{{ card.note }}</p>
        </div>
      </article>
    </section>

    <section class="chart-grid">
      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">员工入职趋势</h3>
            <p class="page-section-subtitle">
              观察组织规模变化，辅助招聘与排班决策。
            </p>
          </div>
        </div>
        <div ref="empChartRef" class="chart-surface"></div>
      </div>

      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">学员入学趋势</h3>
            <p class="page-section-subtitle">
              查看班级扩张节奏，为教学资源投入提供依据。
            </p>
          </div>
        </div>
        <div ref="stuChartRef" class="chart-surface"></div>
      </div>
    </section>

    <section class="chart-grid">
      <div class="page-panel">
        <div class="page-section-header">
          <div>
            <h3 class="page-section-title">学员违纪排行</h3>
            <p class="page-section-subtitle">
              识别风险学员与重点班级，方便进一步跟进。
            </p>
          </div>
        </div>
        <div ref="violationChartRef" class="chart-surface"></div>
      </div>

      <div class="page-table-card">
        <div class="table-panel-header">
          <div>
            <h3 class="page-section-title">违纪学员列表</h3>
            <p class="page-section-subtitle">
              结合次数与扣分，快速锁定需要干预的对象。
            </p>
          </div>
        </div>
        <el-table
          :data="violations"
          border
          style="width: 100%"
          max-height="360"
          size="small"
        >
          <el-table-column
            type="index"
            label="排名"
            width="60"
            align="center"
          />
          <el-table-column prop="name" label="姓名" width="80" align="center" />
          <el-table-column
            prop="clazzName"
            label="班级"
            width="160"
            align="center"
          />
          <el-table-column
            prop="count"
            label="次数"
            width="70"
            align="center"
          />
          <el-table-column
            prop="score"
            label="扣分"
            width="70"
            align="center"
          />
        </el-table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.dashboard-page {
  gap: 20px;
}

.dashboard-hero {
  align-items: center;
}

.dashboard-actions {
  max-width: 280px;
}

.stats-grid {
  display: grid;
  gap: 18px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.stat-panel {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 22px 20px;
  border: 1px solid var(--page-border);
  border-radius: 24px;
  background: rgba(255, 252, 247, 0.88);
  box-shadow: var(--page-card-shadow);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 62px;
  height: 62px;
  border-radius: 20px;
  color: #fffaf5;
}

.stat-content {
  flex: 1;
}

.stat-label {
  margin: 0;
  font-size: 14px;
  color: #7a6b5f;
}

.stat-value {
  margin: 8px 0 6px;
  font-size: 32px;
  line-height: 1;
  color: #2e241d;
}

.stat-note {
  margin: 0;
  font-size: 13px;
  color: #9a897d;
}

.theme-copper .stat-icon {
  background: linear-gradient(135deg, #7c5535, #c29462);
}

.theme-forest .stat-icon {
  background: linear-gradient(135deg, #356c5c, #6aa38f);
}

.theme-amber .stat-icon {
  background: linear-gradient(135deg, #ad6f2d, #d8a15d);
}

.theme-rose .stat-icon {
  background: linear-gradient(135deg, #b65f58, #d69584);
}

.theme-slate .stat-icon {
  background: linear-gradient(135deg, #556069, #82909d);
}

.theme-plum .stat-icon {
  background: linear-gradient(135deg, #6f4f75, #af7cb3);
}

.table-panel-header {
  padding: 20px 22px 0;
}

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 700px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
