<template>
  <div class="dish-container">
    <el-card>
      <div class="header">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入菜品名称"
          clearable
          style="width: 250px"
          @keyup.enter="handleQuery" />
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 200px; margin-left: 10px">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="售卖状态" clearable style="width: 150px; margin-left: 10px">
          <el-option label="启售" :value="1" />
          <el-option label="停售" :value="0" />
        </el-select>
        <el-button type="primary" style="margin-left: 10px" @click="handleQuery">查询</el-button>
        <div style="float: right">
          <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
          <el-button type="primary" @click="handleAdd">新增菜品</el-button>
        </div>
      </div>

      <el-table :data="dishList" v-loading="loading" style="width: 100%; margin-top: 20px" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image :src="scope.row.image" style="width: 40px; height: 40px" :preview-src-list="[scope.row.image]" preview-teleported />
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="菜品分类" />
        <el-table-column prop="price" label="售价">
          <template #default="scope">
            ￥{{ (scope.row.price / 100).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="售卖状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启售' : '停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后操作时间" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">修改</el-button>
            <el-button :type="scope.row.status === 1 ? 'danger' : 'success'" link @click="handleStatusChange(scope.row)">
              {{ scope.row.status === 1 ? '停售' : '启售' }}
            </el-button>
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
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="菜品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择菜品分类" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="菜品口味">
          <el-button type="primary" link @click="addFlavor">添加口味</el-button>
          <div v-for="(flavor, index) in form.flavors" :key="index" style="margin-top: 10px; display: flex; gap: 10px">
            <el-input v-model="flavor.name" placeholder="口味名，如：辣度" style="width: 120px" />
            <el-input v-model="flavor.value" placeholder="口味值，以逗号分隔" style="flex: 1" />
            <el-button type="danger" icon="Delete" circle @click="form.flavors.splice(index, 1)" />
          </div>
        </el-form-item>
        <el-form-item label="菜品图片" prop="image">
          <el-upload
            class="avatar-uploader"
            action="/admin/common/upload"
            :show-file-list="false"
            :headers="{ adminToken: token }"
            :on-success="handleUploadSuccess"
            name="file">
            <img v-if="form.image" :src="form.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="菜品描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入菜品描述" />
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
import { getDishPage, addDish, updateDish, deleteDish, enableOrDisableDish, getDishById } from '@/api/dish'
import { getCategoryList } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const token = localStorage.getItem('token')
const loading = ref(false)
const dishList = ref([])
const categoryList = ref([])
const total = ref(0)
const selectedIds = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  name: '',
  categoryId: undefined,
  status: undefined
})

const form = reactive({
  id: undefined,
  name: '',
  categoryId: '',
  price: 0,
  image: '',
  description: '',
  flavors: []
})

const rules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择菜品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入菜品价格', trigger: 'blur' }],
  image: [{ required: true, message: '请上传菜品图片', trigger: 'change' }]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getDishPage(queryParams)
    dishList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  const res = await getCategoryList(1) // 1 是菜品分类
  categoryList.value = res
}

const handleAdd = () => {
  dialogTitle.value = '新增菜品'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '修改菜品'
  const data = await getDishById(row.id)
  Object.assign(form, data)
  form.price = form.price / 100 // 转换为元显示
  dialogVisible.value = true
}

const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.categoryId = ''
  form.price = 0
  form.image = ''
  form.description = ''
  form.flavors = []
}

const addFlavor = () => {
  form.flavors.push({ name: '', value: '' })
}

const handleUploadSuccess = (res) => {
  if (res.code === 1) {
    form.image = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败：' + res.msg)
  }
}

const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const data = { ...form }
      data.price = Math.round(data.price * 100) // 转换为分
      if (form.id) {
        await updateDish(data)
        ElMessage.success('修改成功')
      } else {
        await addDish(data)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await enableOrDisableDish(newStatus, row.id)
  ElMessage.success('状态更新成功')
  handleQuery()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除菜品 "${row.name}" 吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    await deleteDish(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确认批量删除选中的 ${selectedIds.value.length} 个菜品吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    await deleteDish(selectedIds.value.join(','))
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

onMounted(() => {
  handleQuery()
  fetchCategories()
})
</script>

<style scoped>
.dish-container {
  padding: 4px;
}
.dish-container :deep(.el-card) {
  border-radius: 8px;
}
.header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: border-color 0.2s;
}
.avatar-uploader:hover {
  border-color: #409eff;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>