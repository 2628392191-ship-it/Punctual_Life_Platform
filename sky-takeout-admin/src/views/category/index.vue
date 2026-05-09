<template>
  <div class="category-container">
    <el-card>
      <div class="header">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入分类名称"
          clearable
          style="width: 250px"
          @keyup.enter="handleQuery" />
        <el-select v-model="queryParams.type" placeholder="分类类型" clearable style="width: 150px; margin-left: 10px">
          <el-option label="菜品分类" :value="1" />
          <el-option label="套餐分类" :value="2" />
        </el-select>
        <el-button type="primary" style="margin-left: 10px" @click="handleQuery">查询</el-button>
        <el-button type="primary" style="float: right" @click="handleAdd">新增分类</el-button>
      </div>

      <el-table :data="categoryList" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column label="分类类型">
          <template #default="scope">
            {{ scope.row.type === 1 ? '菜品分类' : '套餐分类' }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="操作时间" />
        <el-table-column prop="sort" label="排序" />
        <el-table-column label="状态">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">修改</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/修改弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择分类类型" style="width: 100%">
            <el-option label="菜品分类" :value="1" />
            <el-option label="套餐分类" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
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
import { getCategoryPage, addCategory, updateCategory, deleteCategory, enableOrDisableCategory } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const categoryList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  name: '',
  type: undefined
})

const form = reactive({
  id: undefined,
  name: '',
  type: 1,
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择分类类型', trigger: 'change' }]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getCategoryPage(queryParams)
    categoryList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增分类'
  form.id = undefined
  form.name = ''
  form.type = 1
  form.sort = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '修改分类'
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateCategory(form)
        ElMessage.success('修改成功')
      } else {
        await addCategory(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除分类 "${row.name}" 吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleStatusChange = async (row) => {
  try {
    await enableOrDisableCategory(row.status, row.id)
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
.category-container {
  padding: 4px;
}
.category-container :deep(.el-card) {
  border-radius: 8px;
}
.header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
</style>