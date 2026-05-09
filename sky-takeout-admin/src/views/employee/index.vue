<template>
  <div class="employee-container">
    <el-card>
      <div class="header">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入员工姓名"
          clearable
          style="width: 250px"
          @keyup.enter="handleQuery" />
        <el-button type="primary" style="margin-left: 10px" @click="handleQuery">查询</el-button>
        <el-button type="primary" style="float: right" @click="handleAdd">添加员工</el-button>
      </div>

      <el-table :data="employeeList" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="状态">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              :disabled="scope.row.username === 'admin'"
              @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后操作时间" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @current-change="handleQuery"
        @size-change="handleQuery" />
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="请输入身份证号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getEmployeePage, addEmployee, updateEmployee, enableOrDisableEmployee } from '@/api/employee'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const employeeList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  name: ''
})

const form = reactive({
  id: undefined,
  username: '',
  name: '',
  phone: '',
  sex: '1',
  idNumber: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  idNumber: [{ required: true, pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getEmployeePage(queryParams)
    employeeList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加员工'
  form.id = undefined
  form.username = ''
  form.name = ''
  form.phone = ''
  form.sex = '1'
  form.idNumber = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑员工'
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateEmployee(form)
        ElMessage.success('修改成功')
      } else {
        await addEmployee(form)
        ElMessage.success('添加成功，默认密码为123456')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleStatusChange = async (row) => {
  try {
    await enableOrDisableEmployee(row.status, row.id)
    ElMessage.success('状态修改成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.employee-container {
  padding: 4px;
}

.employee-container :deep(.el-card) {
  border-radius: 8px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
</style>