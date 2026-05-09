<template>
  <div class="login-container">
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="64" height="64" rx="16" fill="rgba(255,255,255,0.2)"/>
            <path d="M20 44V24l12 10 12-10v20" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M18 18h28" stroke="#fff" stroke-width="3" stroke-linecap="round"/>
            <circle cx="32" cy="30" r="4" fill="#fff"/>
            <path d="M44 42c0 0-1 8-12 8s-12-8-12-8" stroke="#fff" stroke-width="2.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h1>准时达生活平台</h1>
        <p class="tagline">智慧管理 · 高效运营</p>
      </div>
    </div>
    <div class="login-form-side">
      <el-card class="login-card" shadow="none">
        <h2>管理端登录</h2>
        <p class="subtitle">请输入您的账号信息</p>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" class="login-btn" size="large" @click="handleLogin">
              登 录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/admin/employee/login', loginForm)
        localStorage.setItem('token', res.token)
        localStorage.setItem('userInfo', JSON.stringify(res))
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        // 请求失败已经在拦截器中处理过了
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.login-brand {
  flex: 1;
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 50%, #003eb3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-brand::before {
  content: '';
  position: absolute;
  top: -40%;
  right: -10%;
  width: 60%;
  height: 200%;
  background: rgba(255,255,255,0.04);
  border-radius: 50%;
  transform: rotate(-15deg);
}

.login-brand::after {
  content: '';
  position: absolute;
  bottom: -20%;
  left: -5%;
  width: 40%;
  height: 100%;
  background: rgba(255,255,255,0.03);
  border-radius: 50%;
}

.brand-content {
  text-align: center;
  color: #fff;
  position: relative;
  z-index: 1;
}

.brand-icon {
  margin-bottom: 24px;
}

.brand-icon svg {
  width: 80px;
  height: 80px;
}

.brand-content h1 {
  margin: 0;
  font-size: 36px;
  font-weight: 700;
  letter-spacing: 4px;
}

.tagline {
  margin: 16px 0 0;
  font-size: 16px;
  opacity: 0.8;
  letter-spacing: 8px;
}

.login-form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
}

.login-card {
  width: 420px;
  padding: 40px 32px;
  border-radius: 8px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 8px;
  font-size: 24px;
  color: #1a1a1a;
}

.subtitle {
  text-align: center;
  color: #999;
  margin-bottom: 32px;
  font-size: 14px;
}

.login-form {
  margin-top: 8px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
}
</style>
