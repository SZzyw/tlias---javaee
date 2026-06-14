<script setup>
import { ref, watch, onMounted } from "vue";
import {
  queryPageApi,
  addApi,
  queryByIdApi,
  updateApi,
  deleteByIdApi,
} from "@/api/clazz";
import { listApi as listEmpApi } from "@/api/emp";
import { ElMessage, ElMessageBox } from "element-plus";
import { hasPermission } from "@/utils/auth";

const subjects = [
  { name: "Java", value: 1 },
  { name: "前端", value: 2 },
  { name: "大数据", value: 3 },
  { name: "Python", value: 4 },
  { name: "Go", value: 5 },
  { name: "嵌入式", value: 6 },
];

const searchClazz = ref({ name: "", date: [], begin: "", end: "" });
const clazzList = ref([]);
const masters = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const background = ref(true);
const canClazzEdit = hasPermission("clazz:edit");

watch(
  () => searchClazz.value.date,
  (newVal) => {
    if (newVal && newVal.length === 2) {
      searchClazz.value.begin = newVal[0];
      searchClazz.value.end = newVal[1];
    } else {
      searchClazz.value.begin = "";
      searchClazz.value.end = "";
    }
  }
);

onMounted(() => {
  search();
  loadMasters();
});

const loadMasters = async () => {
  const result = await listEmpApi();
  if (result.code) {
    masters.value = result.data;
  }
};

const search = async () => {
  const result = await queryPageApi(
    searchClazz.value.name,
    searchClazz.value.begin,
    searchClazz.value.end,
    currentPage.value,
    pageSize.value
  );
  if (result.code) {
    clazzList.value = result.data.rows;
    total.value = result.data.total;
  }
};

const handleSizeChange = () => {
  search();
};
const handleCurrentChange = () => {
  search();
};

const clear = () => {
  searchClazz.value = { name: "", date: [], begin: "", end: "" };
  search();
};

const subjectName = (val) => {
  const s = subjects.find((s) => s.value === val);
  return s ? s.name : "";
};

const dialogFormVisible = ref(false);
const formTitle = ref("");
const clazz = ref({
  name: "",
  room: "",
  beginDate: "",
  endDate: "",
  masterId: "",
  subject: "",
});
const clazzFormRef = ref();

const addClazz = () => {
  dialogFormVisible.value = true;
  formTitle.value = "新增班级";
  clazz.value = {
    name: "",
    room: "",
    beginDate: "",
    endDate: "",
    masterId: "",
    subject: "",
  };
  if (clazzFormRef.value) clazzFormRef.value.resetFields();
};

const edit = async (id) => {
  formTitle.value = "修改班级";
  if (clazzFormRef.value) clazzFormRef.value.resetFields();
  const result = await queryByIdApi(id);
  if (result.code) {
    dialogFormVisible.value = true;
    clazz.value = result.data;
  }
};

const save = async () => {
  if (!clazzFormRef.value) return;
  clazzFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error("表单校验不通过");
      return;
    }
    const result = clazz.value.id
      ? await updateApi(clazz.value)
      : await addApi(clazz.value);
    if (result.code) {
      ElMessage.success("操作成功");
      dialogFormVisible.value = false;
      search();
    } else {
      ElMessage.error(result.msg);
    }
  });
};

const delById = async (id) => {
  ElMessageBox.confirm("您确认删除该班级吗?", "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const result = await deleteByIdApi(id);
      if (result.code) {
        ElMessage.success("删除成功");
        search();
      } else {
        ElMessage.error(result.msg);
      }
    })
    .catch(() => {
      ElMessage.info("您已取消删除");
    });
};

const rules = ref({
  name: [
    { required: true, message: "班级名称是必填项", trigger: "blur" },
    { min: 2, max: 30, message: "班级名称长度应在2-30位", trigger: "blur" },
  ],
  room: [{ required: true, message: "教室是必填项", trigger: "blur" }],
  beginDate: [
    { required: true, message: "开课时间是必填项", trigger: "change" },
  ],
  endDate: [{ required: true, message: "结课时间是必填项", trigger: "change" }],
  masterId: [{ required: true, message: "请选择班主任", trigger: "change" }],
  subject: [{ required: true, message: "请选择学科", trigger: "change" }],
});
</script>

<template>
  <div class="page-shell">
    <section class="page-header">
      <div class="page-header-copy">
        <p class="page-eyebrow">Classroom Ops</p>
        <h1 class="page-title">班级管理</h1>
        <p class="page-description">
          维护班级名称、学科、班主任与开结课周期，保证班级运营信息在同一页面完整呈现。
        </p>
      </div>
      <div class="page-actions" v-if="canClazzEdit">
        <el-button type="primary" @click="addClazz">+ 新增班级</el-button>
      </div>
    </section>

    <section class="page-card">
      <div class="page-section-header">
        <div>
          <h3 class="page-section-title">筛选条件</h3>
          <p class="page-section-subtitle">
            按班级名称与开课时间快速定位目标班级。
          </p>
        </div>
      </div>
      <el-form :inline="true" :model="searchClazz" class="page-search-form">
        <el-form-item label="班级名称">
          <el-input v-model="searchClazz.name" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="开课时间">
          <el-date-picker
            v-model="searchClazz.date"
            type="daterange"
            range-separator="到"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button type="info" @click="clear">清空</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="page-table-card">
      <div class="table-panel-header">
        <div>
          <h3 class="page-section-title">班级列表</h3>
          <p class="page-section-subtitle">
            统一查看班级基础信息、进度状态与最近更新时间。
          </p>
        </div>
      </div>
      <el-table :data="clazzList" border style="width: 100%">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column
          prop="name"
          label="班级名称"
          width="180"
          align="center"
        />
        <el-table-column prop="room" label="教室" width="120" align="center" />
        <el-table-column
          prop="beginDate"
          label="开课时间"
          width="130"
          align="center"
        />
        <el-table-column
          prop="endDate"
          label="结课时间"
          width="130"
          align="center"
        />
        <el-table-column
          prop="masterName"
          label="班主任"
          width="120"
          align="center"
        />
        <el-table-column label="学科" width="120" align="center">
          <template #default="scope">{{
            subjectName(scope.row.subject)
          }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag
              v-if="scope.row.status === '在读中'"
              type="success"
              size="small"
              >{{ scope.row.status }}</el-tag
            >
            <el-tag
              v-else-if="scope.row.status === '已结课'"
              type="info"
              size="small"
              >{{ scope.row.status }}</el-tag
            >
            <el-tag v-else type="warning" size="small">{{
              scope.row.status
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="updateTime"
          label="最后操作时间"
          width="180"
          align="center"
        />
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button
              v-if="canClazzEdit"
              type="primary"
              size="small"
              @click="edit(scope.row.id)"
            >
              <el-icon><EditPen /></el-icon> 编辑
            </el-button>
            <el-button
              v-if="canClazzEdit"
              type="danger"
              size="small"
              @click="delById(scope.row.id)"
            >
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <section class="page-pagination-card">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 30, 50]"
        :background="background"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </section>
  </div>

  <el-dialog v-model="dialogFormVisible" :title="formTitle" width="600">
    <el-form
      :model="clazz"
      :rules="rules"
      ref="clazzFormRef"
      label-width="100px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班级名称" prop="name">
            <el-input v-model="clazz.name" placeholder="请输入班级名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="教室" prop="room">
            <el-input v-model="clazz.room" placeholder="请输入教室" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开课时间" prop="beginDate">
            <el-date-picker
              v-model="clazz.beginDate"
              type="date"
              style="width: 100%"
              placeholder="选择开课时间"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结课时间" prop="endDate">
            <el-date-picker
              v-model="clazz.endDate"
              type="date"
              style="width: 100%"
              placeholder="选择结课时间"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班主任" prop="masterId">
            <el-select
              v-model="clazz.masterId"
              placeholder="请选择班主任"
              style="width: 100%"
            >
              <el-option
                v-for="m in masters"
                :key="m.id"
                :label="m.name"
                :value="m.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学科" prop="subject">
            <el-select
              v-model="clazz.subject"
              placeholder="请选择学科"
              style="width: 100%"
            >
              <el-option
                v-for="s in subjects"
                :key="s.value"
                :label="s.name"
                :value="s.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped></style>
