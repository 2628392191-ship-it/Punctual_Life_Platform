<template>
  <div class="shop-container">
    <h3 class="section-title">店铺状态管理</h3>

    <el-card shadow="never" class="status-card">
      <div class="status-header">
        <div class="status-label">当前营业状态</div>
        <el-tag :type="isOpen ? 'success' : 'danger'" size="large" effect="dark">
          {{ isOpen ? '营业中' : '已打烊' }}
        </el-tag>
      </div>

      <el-divider />

      <div class="status-body">
        <div class="status-icon-wrap" :class="{ open: isOpen }">
          <el-icon :size="56">
            <component :is="isOpen ? Shop : Close" />
          </el-icon>
        </div>
        <p class="status-desc">
          {{ isOpen ? '店铺正在正常营业，用户可以正常下单。' : '店铺已打烊，用户暂时无法下单。' }}
        </p>
      </div>

      <el-divider />

      <div class="status-actions">
        <el-popconfirm
          title="确定要修改店铺营业状态吗？"
          confirm-button-text="确定"
          cancel-button-text="取消"
          @confirm="toggleStatus">
          <template #reference>
            <el-button
              :type="isOpen ? 'danger' : 'success'"
              size="large"
              :loading="loading"
              :icon="isOpen ? Close : Shop">
              {{ isOpen ? '立即打烊' : '开始营业' }}
            </el-button>
          </template>
        </el-popconfirm>
      </div>
    </el-card>

    <el-card shadow="never" class="tip-card" style="margin-top: 20px">
      <template #header>
        <span><el-icon color="#e6a23c"><WarningFilled /></el-icon> 操作提示</span>
      </template>
      <ul class="tips">
        <li>打烊后，用户端将在首页显示店铺休息中，无法下单。</li>
        <li>打烊期间，已下单的订单仍然会被正常处理。</li>
        <li>建议在确认所有订单处理完毕后再进行打烊操作。</li>
        <li>状态变更会立即生效，无需重启服务。</li>
      </ul>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Shop, Close, WarningFilled } from '@element-plus/icons-vue'
import { getShopStatus, setShopStatus } from '@/api/shop'
import { ElMessage } from 'element-plus'

const isOpen = ref(true)
const loading = ref(false)

const fetchStatus = async () => {
  try {
    const status = await getShopStatus()
    isOpen.value = status === 1
  } catch (e) {
    console.error('获取店铺状态失败', e)
  }
}

const toggleStatus = async () => {
  loading.value = true
  try {
    const newStatus = isOpen.value ? 0 : 1
    await setShopStatus(newStatus)
    isOpen.value = !isOpen.value
    ElMessage.success(isOpen.value ? '店铺已开始营业' : '店铺已打烊')
  } catch (e) {
    ElMessage.error('状态变更失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStatus()
})
</script>

<style scoped>
.shop-container {
  max-width: 600px;
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

.status-card {
  border-radius: 8px;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-label {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.status-body {
  text-align: center;
  padding: 24px 0;
}

.status-icon-wrap {
  width: 100px;
  height: 100px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f5f5;
  color: #999;
}

.status-icon-wrap.open {
  background: #f6ffed;
  color: #52c41a;
}

.status-icon-wrap:not(.open) {
  background: #fff0f0;
  color: #ff4d4f;
}

.status-desc {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.status-actions {
  text-align: center;
}

.tip-card {
  border-radius: 8px;
}

.tips {
  margin: 0;
  padding-left: 18px;
}

.tips li {
  font-size: 13px;
  color: #666;
  line-height: 2;
}
</style>
