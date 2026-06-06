<script setup>
import { ref, onMounted } from 'vue';
import { queryAllApi, addApi, queryByIdApi, updateApi, deleteByIdApi } from '@/api/dept';
import { ElMessage, ElMessageBox } from 'element-plus';
import { hasPermission } from '@/utils/auth';

const canDeptEdit = hasPermission('dept:edit')

//钩子函数
onMounted(() => {
  search();
})

//查询
const deptList = ref([])
const search = async () => {
  const result = await queryAllApi();
  if(result.code){
    deptList.value = result.data;
  }
}

//Dialog对话框
const dialogFormVisible = ref(false);
const formTitle = ref('');
const dept = ref({name: ''});

//新增部门
const addDept = () => {
  dialogFormVisible.value = true;
  formTitle.value = '新增部门';
  dept.value = {name: ''};

  //重置表单的校验规则-提示信息
  if (deptFormRef.value){
    deptFormRef.value.resetFields();
  }
  
}

//保存部门
const save = async () => {
  //表单校验
  if(!deptFormRef.value) return;
  deptFormRef.value.validate(async (valid) => { //valid 表示是否校验通过: true 通过 / false  不通过
    if(valid){ //通过

      let result ;
      if(dept.value.id){ //修改
        result = await updateApi(dept.value);
      }else{ //新增
        result = await addApi(dept.value);
      }

      if(result.code){ //成功
        //提示信息
        ElMessage.success('操作成功');
        //关闭对话框
        dialogFormVisible.value = false;
        //查询
        search();
      }else{ //失败
        ElMessage.error(result.msg);
      }
    }else { //不通过
      ElMessage.error('表单校验不通过');
    }
  })
}


//表单校验
const rules = ref({
  name: [
    { required: true, message: '部门名称是必填项', trigger: 'blur' },
    { min: 2, max: 10, message: '部门名称的长度应该在2-10位', trigger: 'blur' }
  ]
})
const deptFormRef = ref();

//编辑
const edit = async (id) => {
  formTitle.value = '修改部门';
  //重置表单的校验规则-提示信息
  if (deptFormRef.value){
    deptFormRef.value.resetFields();
  }

  const result = await queryByIdApi(id);
  if(result.code){
    dialogFormVisible.value = true;
    dept.value = result.data;
  }
}

//删除
const delById = async (id) => {
  //弹出确认框
  ElMessageBox.confirm('您确认删除该部门吗?','提示',
    { confirmButtonText: '确认',cancelButtonText: '取消',type: 'warning'}
  ).then(async () => { //确认
    const result = await deleteByIdApi(id);
    if(result.code){
      ElMessage.success('删除成功');
      search();
    }else{
      ElMessage.error(result.msg);
    }
  }).catch(() => { //取消
    ElMessage.info('您已取消删除');
  })
}

</script>

<template>
  <div class="page-shell">
    <section class="page-header">
      <div class="page-header-copy">
        <p class="page-eyebrow">Organization</p>
        <h1 class="page-title">部门管理</h1>
        <p class="page-description">统一维护组织结构，为员工归属、角色管理与统计维度提供稳定的基础数据。</p>
      </div>
      <div class="page-actions" v-if="canDeptEdit">
        <el-button type="primary" @click="addDept">+ 新增部门</el-button>
      </div>
    </section>

    <section class="page-table-card">
      <div class="table-panel-header">
        <div>
          <h3 class="page-section-title">部门列表</h3>
          <p class="page-section-subtitle">查看部门名称与最后一次更新时间，保持组织数据清晰一致。</p>
        </div>
      </div>
      <el-table :data="deptList" border style="width: 100%">
        <el-table-column type="index" label="序号" width="100" align="center"/>
        <el-table-column prop="name" label="部门名称" width="300" align="center"/>
        <el-table-column prop="updateTime" label="最后操作时间" width="350" align="center"/>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <el-button v-if="canDeptEdit" type="primary" size="small" @click="edit(scope.row.id)"><el-icon><EditPen /></el-icon> 编辑</el-button>
            <el-button v-if="canDeptEdit" type="danger" size="small" @click="delById(scope.row.id)"><el-icon><Delete /></el-icon> 删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>

  <!-- Dialog对话框 -->
  <el-dialog v-model="dialogFormVisible" :title="formTitle" width="500">
    <el-form :model="dept" :rules="rules" ref="deptFormRef">
      <el-form-item label="部门名称" label-width="80px" prop="name">
        <el-input v-model="dept.name" />
      </el-form-item>
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
