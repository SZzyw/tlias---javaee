<script setup>
import { ref, onMounted } from 'vue'
import { queryPageApi, addApi, queryByIdApi, updateApi, deleteByIdApi, violationApi } from '@/api/stu'
import { listApi as listClazzApi } from '@/api/clazz'
import { ElMessage, ElMessageBox } from 'element-plus'

const degrees = [
  { name: '初中', value: 1 }, { name: '高中', value: 2 },
  { name: '大专', value: 3 }, { name: '本科', value: 4 },
  { name: '硕士', value: 5 }, { name: '博士', value: 6 }
]
const genders = [{ name: '男', value: 1 }, { name: '女', value: 2 }]
const collegeOptions = [{ name: '是', value: 1 }, { name: '否', value: 0 }]

const searchStu = ref({ name: '', degree: '', clazzId: '' })
const stuList = ref([])
const clazzList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const background = ref(true)

onMounted(() => {
  search()
  loadClazzList()
})

const loadClazzList = async () => {
  const result = await listClazzApi()
  if (result.code) clazzList.value = result.data
}

const search = async () => {
  const result = await queryPageApi(
    searchStu.value.name, searchStu.value.degree || null,
    searchStu.value.clazzId || null, currentPage.value, pageSize.value
  )
  if (result.code) {
    stuList.value = result.data.rows
    total.value = result.data.total
  }
}

const handleSizeChange = () => { search() }
const handleCurrentChange = () => { search() }

const clear = () => {
  searchStu.value = { name: '', degree: '', clazzId: '' }
  search()
}

const degreeName = (val) => {
  const d = degrees.find(d => d.value === val)
  return d ? d.name : ''
}

const dialogFormVisible = ref(false)
const violationDialogVisible = ref(false)
const formTitle = ref('')
const student = ref({ name: '', no: '', gender: '', phone: '', idCard: '', isCollege: '',
  address: '', degree: '', graduationDate: '', clazzId: '', violationCount: 0, violationScore: 0 })
const stuFormRef = ref()
const currentStuId = ref(null)
const violationScore = ref(0)

const addStu = () => {
  dialogFormVisible.value = true
  formTitle.value = '新增学员'
  student.value = { name: '', no: '', gender: '', phone: '', idCard: '', isCollege: '',
    address: '', degree: '', graduationDate: '', clazzId: '', violationCount: 0, violationScore: 0 }
  if (stuFormRef.value) stuFormRef.value.resetFields()
}

const edit = async (id) => {
  formTitle.value = '修改学员'
  if (stuFormRef.value) stuFormRef.value.resetFields()
  const result = await queryByIdApi(id)
  if (result.code) {
    dialogFormVisible.value = true
    student.value = result.data
  }
}

const save = async () => {
  if (!stuFormRef.value) return
  stuFormRef.value.validate(async (valid) => {
    if (!valid) { ElMessage.error('表单校验不通过'); return }
    const result = student.value.id ? await updateApi(student.value) : await addApi(student.value)
    if (result.code) {
      ElMessage.success('操作成功')
      dialogFormVisible.value = false
      search()
    } else {
      ElMessage.error(result.msg)
    }
  })
}

const delById = async (id) => {
  ElMessageBox.confirm('您确认删除该学员吗?', '提示',
    { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    const result = await deleteByIdApi(id)
    if (result.code) { ElMessage.success('删除成功'); search() }
    else { ElMessage.error(result.msg) }
  }).catch(() => { ElMessage.info('您已取消删除') })
}

const openViolation = (id) => {
  currentStuId.value = id
  violationScore.value = 0
  violationDialogVisible.value = true
}

const handleViolation = async () => {
  if (!violationScore.value) { ElMessage.warning('请输入扣分'); return }
  const result = await violationApi(currentStuId.value, violationScore.value)
  if (result.code) {
    ElMessage.success('处理成功')
    violationDialogVisible.value = false
    search()
  } else {
    ElMessage.error(result.msg)
  }
}

const rules = ref({
  name: [{ required: true, message: '姓名是必填项', trigger: 'blur' }],
  no: [{ required: true, message: '学号是必填项', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '手机号是必填项', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  idCard: [{ required: true, message: '身份证号是必填项', trigger: 'blur' }],
  clazzId: [{ required: true, message: '请选择班级', trigger: 'change' }]
})
</script>

<template>
  <h1>学员管理</h1>
  <div class="container">
    <el-form :inline="true" :model="searchStu">
      <el-form-item label="姓名">
        <el-input v-model="searchStu.name" placeholder="请输入学员姓名" />
      </el-form-item>
      <el-form-item label="学历">
        <el-select v-model="searchStu.degree" placeholder="请选择" clearable>
          <el-option v-for="d in degrees" :key="d.value" :label="d.name" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="班级">
        <el-select v-model="searchStu.clazzId" placeholder="请选择" clearable>
          <el-option v-for="c in clazzList" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="info" @click="clear">清空</el-button>
      </el-form-item>
    </el-form>
  </div>

  <div class="container">
    <el-button type="primary" @click="addStu">+ 新增学员</el-button>
  </div>

  <div class="container">
    <el-table :data="stuList" border style="width: 100%">
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="name" label="姓名" width="100" align="center" />
      <el-table-column prop="no" label="学号" width="130" align="center" />
      <el-table-column label="性别" width="70" align="center">
        <template #default="scope">{{ scope.row.gender === 1 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="140" align="center" />
      <el-table-column label="学历" width="80" align="center">
        <template #default="scope">{{ degreeName(scope.row.degree) }}</template>
      </el-table-column>
      <el-table-column prop="clazzName" label="班级" width="160" align="center" />
      <el-table-column prop="violationCount" label="违纪次数" width="90" align="center" />
      <el-table-column prop="violationScore" label="违纪扣分" width="90" align="center" />
      <el-table-column prop="updateTime" label="最后操作时间" width="180" align="center" />
      <el-table-column label="操作" width="280" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="edit(scope.row.id)">
            <el-icon><EditPen /></el-icon> 编辑
          </el-button>
          <el-button type="warning" size="small" @click="openViolation(scope.row.id)">
            <el-icon><WarningFilled /></el-icon> 违纪
          </el-button>
          <el-button type="danger" size="small" @click="delById(scope.row.id)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <div class="container">
    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
      :page-sizes="[5, 10, 20, 30, 50]" :background="background"
      layout="total, sizes, prev, pager, next, jumper" :total="total"
      @size-change="handleSizeChange" @current-change="handleCurrentChange" />
  </div>

  <el-dialog v-model="dialogFormVisible" :title="formTitle" width="750">
    <el-form :model="student" :rules="rules" ref="stuFormRef" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="student.name" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学号" prop="no">
            <el-input v-model="student.no" placeholder="请输入学号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="student.gender" placeholder="请选择" style="width: 100%">
              <el-option v-for="g in genders" :key="g.value" :label="g.name" :value="g.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="student.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="student.idCard" placeholder="请输入身份证号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="毕业院校">
            <el-select v-model="student.isCollege" placeholder="是否来自院校" style="width: 100%">
              <el-option v-for="c in collegeOptions" :key="c.value" :label="c.name" :value="c.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学历">
            <el-select v-model="student.degree" placeholder="请选择学历" style="width: 100%">
              <el-option v-for="d in degrees" :key="d.value" :label="d.name" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="班级" prop="clazzId">
            <el-select v-model="student.clazzId" placeholder="请选择班级" style="width: 100%">
              <el-option v-for="c in clazzList" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="毕业时间">
            <el-date-picker v-model="student.graduationDate" type="date" style="width: 100%"
              placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系地址">
            <el-input v-model="student.address" placeholder="请输入联系地址" />
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

  <el-dialog v-model="violationDialogVisible" title="违纪处理" width="400">
    <el-form label-width="100px">
      <el-form-item label="扣分">
        <el-input-number v-model="violationScore" :min="1" :max="100" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="violationDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleViolation">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.container { margin: 10px 0px; }
</style>
