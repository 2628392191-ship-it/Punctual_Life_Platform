<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <h2>准时达生活平台</h2>
        <p>管理端</p>
      </div>
      <el-menu
        :default-active="route.path"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router>
        <el-menu-item index="/">
          <el-icon><Odometer /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/employee">
          <el-icon><User /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Files /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/dish">
          <el-icon><Food /></el-icon>
          <span>菜品管理</span>
        </el-menu-item>
        <el-menu-item index="/setmeal">
          <el-icon><Box /></el-icon>
          <span>套餐管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><DataLine /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/shop">
          <el-icon><Shop /></el-icon>
          <span>店铺状态</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="welcome-text">欢迎回来，{{ userInfo.name || '管理员' }}</span>
        </div>
        <div class="right-menu">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-icon style="margin-right: 4px"><UserFilled /></el-icon>
              {{ userInfo.name || '管理员' }}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Odometer, User, Files, Food, Box, List, DataLine, ArrowDown, UserFilled, Shop } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await request.post('/admin/employee/logout')
    } catch (e) {
      console.log('Logout API failed, continuing local logout')
    }
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
}
.logo {
  height: 72px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(180deg, rgba(255,255,255,0.06) 0%, transparent 100%);
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 3px;
}
.logo p {
  margin: 2px 0 0;
  font-size: 11px;
  color: rgba(255,255,255,0.5);
  letter-spacing: 2px;
}
.el-menu-vertical {
  border-right: none;
}
.header {
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.header-left {
  display: flex;
  align-items: center;
}
.welcome-text {
  color: #666;
  font-size: 14px;
}
.right-menu {
  cursor: pointer;
}
.el-dropdown-link {
  display: flex;
  align-items: center;
  color: #333;
  font-size: 14px;
}
.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>