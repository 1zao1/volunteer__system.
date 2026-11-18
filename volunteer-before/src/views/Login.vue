<template>
  <div class="auth-card">
    <h3 class="text-center text-success mb-4"><i class="bi bi-heart-pulse"></i> 志愿汇登录</h3>
    <form @submit.prevent="onSubmit">
      <div class="mb-3">
        <label class="form-label">用户名</label>
        <input v-model="form.username" class="form-control" required>
      </div>
      <div class="mb-3">
        <label class="form-label">密码</label>
        <input v-model="form.password" type="password" class="form-control" required>
      </div>
      <button class="btn btn-success w-100" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </form>
    <div class="text-center mt-3">
      还没有账号？<router-link to="/register">立即注册</router-link>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { useAuthStore } from '../store/auth'

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const router = useRouter()
const auth = useAuthStore()

async function onSubmit() {
  loading.value = true
  try {
    const res = await api.login(form)
    auth.setSession(res.token, { id: res.id, name: res.name, role: res.role })
    router.push('/activities')
  } catch (e) {
    alert(e.message)
  } finally {
    loading.value = false
  }
}
</script>