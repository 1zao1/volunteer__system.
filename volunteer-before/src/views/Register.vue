<template>
  <div class="auth-container">
    <main class="auth-content">
      <div class="auth-card">
        <div class="auth-header">
          <p class="eyebrow">JOIN US</p>
          <h1>注册账号</h1>
          <p class="subtitle">填写基本信息，并选择你要注册的身份。</p>
        </div>

        <div class="form-box">
          <form @submit.prevent="onSubmit">
            <!-- 身份选择 -->
            <div class="field">
              <label class="label">注册身份<span class="req">*</span></label>
              <div class="role-picker">
                <button
                  v-for="r in roles"
                  :key="r.key"
                  type="button"
                  class="role-card"
                  :class="{ active: form.role === r.key }"
                  @click="form.role = r.key"
                >
                  <i class="bi" :class="r.icon"></i>
                  <span class="t">{{ r.name }}</span>
                  <span class="d">{{ r.desc }}</span>
                </button>
              </div>
            </div>

            <div class="field">
              <label class="label">用户名<span class="req">*</span></label>
              <input v-model.trim="form.username" class="input" placeholder="2-20 个字符，建议用拼音或英文" />
            </div>
            <div class="field">
              <label class="label">手机号<span class="req">*</span></label>
              <input v-model.trim="form.phone" class="input" placeholder="11 位手机号" maxlength="11" />
            </div>
            <div class="field">
              <label class="label">密码<span class="req">*</span></label>
              <input v-model.trim="form.password" type="password" class="input" placeholder="至少 6 位" />
            </div>
            <div class="field">
              <label class="label">技能标签</label>
              <input v-model.trim="form.skills" class="input" placeholder="如：医疗急救,摄影,英语口语" />
              <div class="hint">多个标签用逗号分隔，便于组织者按技能匹配岗位</div>
            </div>

            <button class="btn-submit" :disabled="loading">
              <i class="bi bi-person-plus" style="margin-right: 6px;"></i>
              {{ loading ? '提交中...' : '完成注册' }}
            </button>
          </form>
        </div>

        <div class="auth-footer">
          已有账号？<router-link to="/login">返回登录</router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { toast } from '../utils/toast'

const router = useRouter()
const loading = ref(false)
const form = reactive({ role: 'volunteer', username: '', phone: '', password: '', skills: '' })

const roles = [
  { key: 'volunteer', name: '志愿者', desc: '报名活动、累计工时', icon: 'bi-person-heart' },
  { key: 'organizer', name: '组织者', desc: '发布活动、审核报名', icon: 'bi-megaphone' },
  { key: 'admin', name: '管理员', desc: '需总管理员认证', icon: 'bi-shield-check' }
]

async function onSubmit() {
  if (!form.username || !form.phone || !form.password) return toast.error('请填写完整信息')
  if (!/^1\d{10}$/.test(form.phone)) return toast.error('手机号格式不正确')
  if (form.password.length < 6) return toast.error('密码至少 6 位')

  loading.value = true
  try {
    await api.register({
      username: form.username,
      phone: form.phone,
      password: form.password,
      role: form.role,
      skills: form.skills.split(',').map((s) => s.trim()).filter(Boolean)
    })
    toast.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    toast.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 关键修复：使用 min-height 并允许滚动 */
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f8fafc;
  padding: 60px 20px; /* 增加上下 padding，防止小屏幕贴边 */
  box-sizing: border-box;
}

.auth-content {
  width: 100%;
  display: flex;
  justify-content: center;
}

.auth-card {
  width: 100%;
  max-width: 540px; /* 注册页内容较多，稍微加宽 */
  background: #ffffff;
  padding: 50px 45px;
  border-radius: 16px;
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.06);
  box-sizing: border-box;
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
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

/* 身份选择区域 */
.role-picker {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.role-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 10px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #fff;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.2s ease;
}

.role-card:hover {
  border-color: #10b981;
}

.role-card.active {
  border-color: #10b981;
  background: #f0fdf4;
  color: #10b981;
}

.role-card .bi {
  font-size: 22px;
  margin-bottom: 8px;
}

.role-card .t {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.role-card .d {
  font-size: 11px;
  line-height: 1.2;
  text-align: center;
  color: #9ca3af;
}

.role-card.active .d {
  color: #6b7280;
}

/* 表单基础样式 */
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

.req {
  color: #ef4444;
  margin-left: 2px;
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

.hint {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 6px;
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