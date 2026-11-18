<template>
  <div class="auth-container">
    <main class="auth-content">
      <div class="auth-card">
        <div class="auth-header">
          <p class="eyebrow">WELCOME GUYS</p>
          <h1>登录予光</h1>
          <p class="subtitle">登录后按角色进入不同的工作台。</p>
        </div>

        <div class="form-box">
          <form @submit.prevent="onSubmit">
            <div class="field">
              <label class="label">用户名</label>
              <input 
                v-model.trim="form.username" 
                class="input" 
                type="text" 
                placeholder="请输入用户名" 
                autocomplete="username" 
              />
            </div>
            <div class="field">
              <label class="label">密码</label>
              <input 
                v-model.trim="form.password" 
                type="password" 
                class="input" 
                placeholder="请输入密码" 
                autocomplete="current-password" 
              />
            </div>
            
            <button 
              class="btn-submit" 
              :disabled="loading"
            >
              <i class="bi bi-box-arrow-in-right" style="margin-right: 6px;"></i>
              {{ loading ? '登录中...' : '登录' }}
            </button>
          </form>
        </div>

        <div class="auth-footer">
          还没有账号？<router-link to="/register">注册账号</router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { useAuthStore } from '../store/auth'
import { toast } from '../utils/toast'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

async function onSubmit() {
  if (!form.username || !form.password) return toast.error('请填写用户名和密码')
  loading.value = true
  try {
    const res = await api.login({ username: form.username, password: form.password })
    auth.setSession(res.token, res)
    toast.success(`欢迎回来，${res.nickname || res.name}`)
    router.push(res.role === 'volunteer' ? '/activities' : '/dashboard')
  } catch (e) {
    toast.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 关键修复：全屏 Flex 居中，允许页面出现滚动条防止被截断 */
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f8fafc;
  padding: 40px 20px;
  box-sizing: border-box;
}

.auth-content {
  width: 100%;
  display: flex;
  justify-content: center;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  background: #ffffff;
  padding: 50px 45px;
  border-radius: 16px;
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.06);
}

.auth-header {
  text-align: center;
  margin-bottom: 36px;
}

.eyebrow {
  font-size: 12px;
  color: #10b981;
  letter-spacing: 2px;
  font-weight: 700;
  margin-bottom: 12px;
}

.auth-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
}

.field {
  margin-bottom: 20px;
}

.label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.input {
  width: 100%;
  height: 46px;
  padding: 0 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  color: #111827;
  outline: none;
  background-color: #f9fafb;
  transition: all 0.2s;
  box-sizing: border-box;
}

.input:focus {
  border-color: #10b981;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.btn-submit {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: 8px;
  background-color: #10b981;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 12px;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background-color: #059669;
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 32px;
  text-align: center;
  font-size: 14px;
  color: #6b7280;
}

.auth-footer a {
  color: #10b981;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
}
</style>