<template>
  <div class="order-container">
    <el-card>
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待接单" name="2" />
        <el-tab-pane label="待派送" name="3" />
        <el-tab-pane label="派送中" name="4" />
        <el-tab-pane label="已完成" name="5" />
        <el-tab-pane label="已取消" name="6" />
      </el-tabs>

      <div class="search-bar">
        <el-input v-model="queryParams.number" placeholder="订单号" style="width: 200px" clearable />
        <el-input v-model="queryParams.phone" placeholder="手机号" style="width: 200px; margin-left: 10px" clearable />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-left: 10px"
          value-format="YYYY-MM-DD HH:mm:ss" />
        <el-button type="primary" style="margin-left: 10px" @click="handleQuery">查询</el-button>
      </div>

      <el-table :data="orderList" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="number" label="订单号" width="180" />
        <el-table-column label="订单状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="orderTime" label="下单时间" width="180" />
        <el-table-column label="实收金额">
          <template #default="scope">￥{{ scope.row.amount }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button type="primary" link @click="handleDetail(scope.row)">详情</el-button>
            <el-button v-if="scope.row.status === 2" type="success" link @click="handleConfirm(scope.row)">接单</el-button>
            <el-button v-if="scope.row.status === 2" type="danger" link @click="handleReject(scope.row)">拒单</el-button>
            <el-button v-if="scope.row.status === 3" type="primary" link @click="handleDelivery(scope.row)">派送</el-button>
            <el-button v-if="scope.row.status === 4" type="success" link @click="handleComplete(scope.row)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
        @current-change="handleQuery" />
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" v-model="detailVisible" width="700px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.number }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.orderTime }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentOrder.consignee }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentOrder.phone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
          <el-descriptions-item label="订单备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top: 20px">菜品明细</h4>
        <el-table :data="currentOrder.orderDetailList" border stripe>
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="dishFlavor" label="口味" />
          <el-table-column prop="number" label="数量" width="80" />
          <el-table-column label="单价" width="100">
            <template #default="scope">￥{{ scope.row.amount }}</template>
          </el-table-column>
        </el-table>

        <div style="text-align: right; margin-top: 20px; font-size: 18px">
          合计：<span style="color: #f56c6c; font-weight: bold">￥{{ currentOrder.amount }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getOrderPage, getOrderDetail, confirmOrder, rejectOrder, deliveryOrder, completeOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const activeStatus = ref('all')
const dateRange = ref([])
const detailVisible = ref(false)
const currentOrder = ref(null)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  number: '',
  phone: '',
  status: undefined,
  beginTime: undefined,
  endTime: undefined
})

const handleQuery = async () => {
  loading.value = true
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.beginTime = dateRange.value[0]
    queryParams.endTime = dateRange.value[1]
  } else {
    queryParams.beginTime = undefined
    queryParams.endTime = undefined
  }
  queryParams.status = activeStatus.value === 'all' ? undefined : Number(activeStatus.value)

  try {
    const res = await getOrderPage(queryParams)
    orderList.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  queryParams.page = 1
  handleQuery()
}

const getStatusLabel = (status) => {
  const labels = { 1: '待付款', 2: '待接单', 3: '已接单', 4: '派送中', 5: '已完成', 6: '已取消' }
  return labels[status] || '未知'
}

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'danger', 3: 'primary', 4: 'info', 5: 'success', 6: 'info' }
  return types[status] || ''
}

const handleDetail = async (row) => {
  currentOrder.value = await getOrderDetail(row.id)
  detailVisible.value = true
}

const handleConfirm = (row) => {
  ElMessageBox.confirm('确认接单吗？', '提示').then(async () => {
    await confirmOrder({ id: row.id })
    ElMessage.success('已接单')
    handleQuery()
  })
}

const handleReject = (row) => {
  ElMessageBox.prompt('请输入拒单原因', '拒单提示', { confirmButtonText: '确定', cancelButtonText: '取消' })
    .then(async ({ value }) => {
      await rejectOrder({ id: row.id, rejectionReason: value })
      ElMessage.success('已拒单')
      handleQuery()
    })
}

const handleDelivery = async (row) => {
  await deliveryOrder(row.id)
  ElMessage.success('已开始派送')
  handleQuery()
}

const handleComplete = async (row) => {
  await completeOrder(row.id)
  ElMessage.success('订单已完成')
  handleQuery()
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.order-container { padding: 4px; }

.order-container :deep(.el-card) {
  border-radius: 8px;
}

.search-bar {
  margin: 16px 0;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.order-detail { padding: 10px; }
</style>