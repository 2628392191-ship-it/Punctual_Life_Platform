<template>
  <div class="report-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h3>数据统计报表</h3>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        :shortcuts="shortcuts"
        @change="handleDateChange" />
    </div>

    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background: #e6f7ff">
            <el-icon size="28" color="#1677ff"><Money /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-label">营业额统计</div>
            <div class="stat-value">￥{{ totalTurnover.toFixed(2) }}</div>
            <div class="stat-desc">所选期间总营业额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background: #f6ffed">
            <el-icon size="28" color="#52c41a"><Tickets /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-label">订单统计</div>
            <div class="stat-value">{{ totalOrders }}</div>
            <div class="stat-desc">所选期间订单总量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background: #fff0f6">
            <el-icon size="28" color="#eb2f96"><User /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-label">新用户统计</div>
            <div class="stat-value">{{ totalNewUsers }}</div>
            <div class="stat-desc">所选期间新增用户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 下半部分 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="card-title">
              <el-icon color="#1677ff"><Trophy /></el-icon>
              <span>销量 Top 10</span>
            </div>
          </template>
          <el-table :data="top10Data" stripe>
            <el-table-column type="index" label="排名" width="60" align="center" />
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="number" label="销量" width="100" align="center">
              <template #default="scope">
                <el-tag type="primary">{{ scope.row.number }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="card-title">
              <el-icon color="#1677ff"><MagicStick /></el-icon>
              <span>今日经营建议</span>
              <el-tag size="small" type="success" style="margin-left: 8px">AI</el-tag>
            </div>
          </template>
          <div v-if="loading" v-loading="true" style="height: 120px"></div>
          <div v-else class="ai-suggestion">
            <p class="ai-intro">根据当前经营数据，系统为您提供以下建议：</p>
            <div class="suggestion-list">
              <div class="suggestion-item" v-for="(tip, index) in suggestions" :key="index">
                <span class="suggestion-num">{{ index + 1 }}</span>
                <span>{{ tip }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Money, Tickets, User, Trophy, MagicStick } from '@element-plus/icons-vue'
import { getTurnoverStatistics, getUserStatistics, getOrdersStatistics, getTop10Statistics } from '@/api/workspace'
import dayjs from 'dayjs'

const loading = ref(false)
const dateRange = ref([
  dayjs().subtract(6, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
])

const totalTurnover = ref(0)
const totalOrders = ref(0)
const totalNewUsers = ref(0)
const top10Data = ref([])
const suggestions = ref([
  "近期营业额稳定，建议保持当前促销力度。",
  "下午 2:00-4:00 是流量低谷，可考虑推出下午茶限时特惠套餐。",
  "分类 '热销' 下的菜品点击率极高，建议作为外卖首屏推荐。"
])

const shortcuts = [
  { text: '最近一周', value: () => [dayjs().subtract(6, 'day').toDate(), dayjs().toDate()] },
  { text: '最近一月', value: () => [dayjs().subtract(29, 'day').toDate(), dayjs().toDate()] }
]

const fetchData = async () => {
  loading.value = true
  const params = { begin: dateRange.value[0], end: dateRange.value[1] }
  try {
    const [turnover, users, orders, top10] = await Promise.all([
      getTurnoverStatistics(params),
      getUserStatistics(params),
      getOrdersStatistics(params),
      getTop10Statistics(params)
    ])

    // 处理营业额 (后端返回的是逗号分隔字符串)
    totalTurnover.value = turnover.turnoverList.split(',').reduce((acc, cur) => acc + Number(cur || 0), 0)
    totalNewUsers.value = users.newUserList.split(',').reduce((acc, cur) => acc + Number(cur || 0), 0)
    totalOrders.value = orders.totalOrderCount

    // 处理 Top 10
    const names = top10.nameList.split(',')
    const numbers = top10.numberList.split(',')
    top10Data.value = names.map((name, i) => ({ name, number: numbers[i] })).filter(item => item.name)

  } finally {
    loading.value = false
  }
}

const handleDateChange = () => {
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.report-container { padding: 4px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}

.page-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  padding-left: 12px;
  border-left: 3px solid #1677ff;
  line-height: 1;
}

.stat-row { margin-bottom: 0; }

.stat-card {
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  flex-shrink: 0;
}

.stat-body { flex: 1; }

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 12px;
  color: #999;
}

.content-card { border-radius: 8px; }

.card-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.ai-suggestion { padding: 4px 0; }

.ai-intro {
  color: #666;
  margin-bottom: 16px;
  font-size: 14px;
}

.suggestion-list { display: flex; flex-direction: column; gap: 10px; }

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 13px;
  color: #555;
  line-height: 1.6;
}

.suggestion-num {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1677ff;
  color: #fff;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}
</style>