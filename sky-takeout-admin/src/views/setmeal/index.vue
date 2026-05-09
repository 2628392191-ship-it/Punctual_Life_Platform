<template>
  <div class="setmeal-container">
    <el-card>
      <div class="header">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入套餐名称"
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
        <el-button type="primary" style="float: right" @click="handleAdd">新增套餐</el-button>
      </div>

      <el-table :data="setmealList" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="name" label="套餐名称" />
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image :src="scope.row.image" style="width: 40px; height: 40px" :preview-src-list="[scope.row.image]" preview-teleported />
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="套餐分类" />
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="套餐分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择套餐分类" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="套餐价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="套餐菜品">
          <el-button type="primary" size="small" @click="openDishPicker">添加菜品</el-button>
          <el-table :data="form.setmealDishes" border style="margin-top: 10px">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="price" label="原价(单价)">
              <template #default="scope">￥{{ (scope.row.price / 100).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="份数" width="150">
              <template #default="scope">
                <el-input-number v-model="scope.row.copies" :min="1" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button type="danger" link @click="form.setmealDishes.splice(scope.$index, 1)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="套餐图片" prop="image">
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
        <el-form-item label="套餐描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 菜品选择器弹窗 -->
    <el-dialog title="选择菜品" v-model="dishPickerVisible" width="600px">
      <el-form inline>
        <el-form-item label="分类">
          <el-select v-model="pickerParams.categoryId" style="width: 150px" @change="fetchPickerDishes">
            <el-option v-for="item in dishCategoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="pickerDishList" @selection-change="handlePickerSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column prop="price" label="价格">
          <template #default="scope">￥{{ (scope.row.price / 100).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="dishPickerVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDishSelection">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSetmealPage, addSetmeal, updateSetmeal, deleteSetmeal, enableOrDisableSetmeal, getSetmealById } from '@/api/setmeal'
import { getCategoryList } from '@/api/category'
import { getDishList } from '@/api/dish'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const token = localStorage.getItem('token')
const loading = ref(false)
const setmealList = ref([])
const categoryList = ref([]) // 套餐分类
const total = ref(0)
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
  setmealDishes: []
})

const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择套餐分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入套餐价格', trigger: 'blur' }],
  image: [{ required: true, message: '请上传套餐图片', trigger: 'change' }]
}

// 菜品选择器相关
const dishPickerVisible = ref(false)
const dishCategoryList = ref([])
const pickerDishList = ref([])
const pickerSelectedDishes = ref([])
const pickerParams = reactive({ categoryId: undefined })

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getSetmealPage(queryParams)
    setmealList.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  categoryList.value = await getCategoryList(2) // 2 套餐分类
  dishCategoryList.value = await getCategoryList(1) // 1 菜品分类
}

const handleAdd = () => {
  dialogTitle.value = '新增套餐'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '修改套餐'
  const data = await getSetmealById(row.id)
  Object.assign(form, data)
  form.price = form.price / 100
  dialogVisible.value = true
}

const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.categoryId = ''
  form.price = 0
  form.image = ''
  form.description = ''
  form.setmealDishes = []
}

const handleUploadSuccess = (res) => {
  if (res.code === 1) form.image = res.data
}

const openDishPicker = () => {
  dishPickerVisible.value = true
  if (dishCategoryList.value.length && !pickerParams.categoryId) {
    pickerParams.categoryId = dishCategoryList.value[0].id
    fetchPickerDishes()
  }
}

const fetchPickerDishes = async () => {
  pickerDishList.value = await getDishList({ categoryId: pickerParams.categoryId, status: 1 })
}

const handlePickerSelectionChange = (val) => {
  pickerSelectedDishes.value = val
}

const confirmDishSelection = () => {
  pickerSelectedDishes.value.forEach(dish => {
    const exists = form.setmealDishes.find(item => item.dishId === dish.id)
    if (!exists) {
      form.setmealDishes.push({
        dishId: dish.id,
        name: dish.name,
        price: dish.price,
        copies: 1
      })
    }
  })
  dishPickerVisible.value = false
}

const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const data = { ...form }
      data.price = Math.round(data.price * 100)
      if (form.id) {
        await updateSetmeal(data)
        ElMessage.success('修改成功')
      } else {
        await addSetmeal(data)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  await enableOrDisableSetmeal(newStatus, row.id)
  ElMessage.success('状态修改成功')
  handleQuery()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除套餐 "${row.name}" 吗？`, '警告', { type: 'warning' })
    .then(async () => {
      await deleteSetmeal(row.id)
      ElMessage.success('删除成功')
      handleQuery()
    })
}

onMounted(() => {
  handleQuery()
  fetchCategories()
})
</script>

<style scoped>
.setmeal-container { padding: 4px; }
.setmeal-container :deep(.el-card) { border-radius: 8px; }
.header { display: flex; align-items: center; margin-bottom: 16px; }
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px; height: 100px;
  display: flex; justify-content: center; align-items: center;
  transition: border-color 0.2s;
}
.avatar-uploader:hover { border-color: #409eff; }
.avatar { width: 100px; height: 100px; display: block; object-fit: cover; }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; }
</style>