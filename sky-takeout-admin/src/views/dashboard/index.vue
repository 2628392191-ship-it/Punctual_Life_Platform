<template>
  <div class="dashboard-container">
    <!-- 今日数据概览 -->
    <h3 class="section-title">今日数据概览</h3>
    <el-row :gutter="20">
      <el-col :span="4.8" v-for="(item, index) in businessDataItems" :key="index">
        <el-card shadow="hover" class="data-card">
          <div class="card-icon" :style="{ background: item.bg }">
            <el-icon :size="24"><component :is="item.icon" /></el-icon>
          </div>
          <div class="card-label">{{ item.label }}</div>
          <div class="card-value">{{ item.value }}<span class="card-unit">{{ item.unit }}</span></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单统计与概览 -->
    <h3 class="section-title">经营概况</h3>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never" class="overview-card">
          <template #header>
            <div class="overview-header">
              <el-icon color="#409eff"><List /></el-icon>
              <span>订单管理概览</span>
            </div>
          </template>
          <div class="overview-grid">
            <div class="overview-item" v-for="o in orderItems" :key="o.label">
              <div class="ov-label">{{ o.label }}</div>
              <div class="ov-value" :style="{ color: o.color }">{{ o.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="overview-card">
          <template #header>
            <div class="overview-header">
              <el-icon color="#67c23a"><Box /></el-icon>
              <span>菜品 / 套餐总览</span>
            </div>
          </template>
          <div class="overview-grid">
            <div class="overview-item" v-for="m in menuItems" :key="m.label">
              <div class="ov-label">{{ m.label }}</div>
              <div class="ov-value" :style="{ color: m.color }">{{ m.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI 经营分析入口 -->
    <el-card shadow="never" class="ai-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <div class="ai-header-left">
            <el-icon color="#1677ff"><MagicStick /></el-icon>
            <span>AI 智能经营分析</span>
          </div>
          <el-tag type="success" size="small">Beta</el-tag>
        </div>
      </template>
      <div class="ai-content">
        <div class="ai-highlight">
          <p>系统已集成智能规则引擎，可根据近期经营指标自动识别风险并提供优化建议。</p>
        </div>
        <el-button type="primary" @click="goToAnalysis">
          进入 AI 分析中心
          <el-icon style="margin-left: 4px"><Right /></el-icon>
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Money, Tickets, DataLine, Wallet, User, List, Box, MagicStick, Right } from '@element-plus/icons-vue'
import { getBusinessData, getOrderOverview, getDishOverview, getSetmealOverview } from '@/api/workspace'

const router = useRouter()
const businessData = ref({})
const orderOverview = ref({})
const dishOverview = ref({})
const setmealOverview = ref({})

const businessDataItems = computed(() => [
  { label: '今日营业额', value: (businessData.value.turnover || 0).toFixed(2), unit: '元', icon: Money, bg: '#e6f7ff' },
  { label: '有效订单数', value: businessData.value.validOrderCount || 0, unit: '单', icon: Tickets, bg: '#f6ffed' },
  { label: '订单完成率', value: ((businessData.value.orderCompletionRate || 0) * 100).toFixed(1), unit: '%', icon: DataLine, bg: '#fff7e6' },
  { label: '平均客单价', value: (businessData.value.unitPrice || 0).toFixed(2), unit: '元', icon: Wallet, bg: '#f0f5ff' },
  { label: '今日新增用户', value: businessData.value.newUsers || 0, unit: '人', icon: User, bg: '#fff0f6' }
])

const orderItems = computed(() => [
  { label: '待接单', value: orderOverview.value.waitingOrders || 0, color: '#f56c6c' },
  { label: '待派送', value: orderOverview.value.deliveredOrders || 0, color: '#e6a23c' },
  { label: '已完成', value: orderOverview.value.completedOrders || 0, color: '#67c23a' },
  { label: '已取消', value: orderOverview.value.cancelledOrders || 0, color: '#909399' },
  { label: '全部订单', value: orderOverview.value.allOrders || 0, color: '#409eff' }
])

const menuItems = computed(() => [
  { label: '已启售菜品', value: dishOverview.value.sold || 0, color: '#67c23a' },
  { label: '已停售菜品', value: dishOverview.value.discontinued || 0, color: '#f56c6c' },
  { label: '已启售套餐', value: setmealOverview.value.sold || 0, color: '#67c23a' },
  { label: '已停售套餐', value: setmealOverview.value.discontinued || 0, color: '#f56c6c' }
])

const initData = async () => {
  try {
    const [biz, order, dish, setmeal] = await Promise.all([
      getBusinessData(),
      getOrderOverview(),
      getDishOverview(),
      getSetmealOverview()
    ])
    businessData.value = biz
    orderOverview.value = order
    dishOverview.value = dish
    setmealOverview.value = setmeal
  } catch (error) {
    console.error('Failed to load workspace data', error)
  }
}

const goToAnalysis = () => {
  router.push('/report')
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 4px;
}

.section-title {
  margin: 0 0 16px 0;
  padding-left: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-left: 3px solid #1677ff;
  line-height: 1;
}

.data-card {
  text-align: center;
  background-color: #fff;
  border-radius: 8px;
  transition: transform 0.2s;
}
.data-card:hover {
  transform: translateY(-2px);
}

.card-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: #fff;
}

.card-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.card-value {
  font-size: 26px;
  font-weight: 700;
  color: #1a1a1a;
}

.card-unit {
  font-size: 14px;
  font-weight: 400;
  color: #999;
  margin-left: 4px;
}

.overview-card {
  border-radius: 8px;
}

.overview-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 16px;
  padding: 8px 0;
}

.overview-item {
  text-align: center;
}

.ov-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.ov-value {
  font-size: 28px;
  font-weight: 700;
}

.ai-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.ai-content {
  text-align: center;
  padding: 16px 0 8px;
}

.ai-highlight {
  display: inline-block;
  background: linear-gradient(135deg, #f0f5ff 0%, #e6f7ff 100%);
  border-radius: 8px;
  padding: 16px 24px;
  margin-bottom: 20px;
}

.ai-highlight p {
  color: #555;
  margin: 0;
  font-size: 14px;
}
</style>