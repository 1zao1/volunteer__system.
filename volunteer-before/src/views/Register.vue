<template>
  <div class="auth-card">
    <h3 class="text-center text-success mb-4">志愿者注册</h3>
    <form @submit.prevent="onSubmit">
      <div class="mb-3">
        <label class="form-label">用户名</label>
        <input v-model="form.username" class="form-control" required>
      </div>
      <div class="mb-3">
        <label class="form-label">手机号</label>
        <input v-model="form.phone" class="form-control" required>
      </div>
      <div class="mb-3">
        <label class="form-label">密码</label>
        <input v-model="form.password" type="password" class="form-control" minlength="6" required>
      </div>
      <div class="mb-3">
        <label class="form-label">技能标签（逗号分隔）</label>
        <input v-model="form.skills" class="form-control" placeholder="如：医疗,摄影">
      </div>
      <button class="btn btn-success w-100">注册</button>
    </form>
    <div class="text-center mt-3">
      已有账号？<router-link to="/login">去登录</router-link>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const form = reactive({ username: '', phone: '', password: '', skills: '' })
const router = useRouter()

async function onSubmit() {
  try {
    await api.register({ 
      ...form, 
      skills: form.skills.split(',').map(s => s.trim()).filter(Boolean) 
    })
    alert('注册成功')
    router.push('/login')
  } catch (e) {
    alert(e.message)
  }
}
</script>