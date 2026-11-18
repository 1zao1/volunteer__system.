<template>
  <div class="auth">
    <aside class="auth-side">
      <div>
        <div class="brand">
          <span class="logo-mark"><i class="bi bi-heart-pulse"></i></span>
          <span>志愿汇 · Volunteer Hub</span>
        </div>
        <h2>让每一次<em>付出</em><br />都被看见、被记录</h2>
        <p>发布活动、招募志愿者、审核报名、认定工时，一个平台跑完整个公益链路。</p>

        <div class="demo-list">
          <div class="demo-label">体 验 账 号 · 点 击 即 登 录</div>
          <div
            v-for="d in demos"
            :key="d.username"
            class="demo"
            @click="quickLogin(d)"
          >
            <span class="ico"><i class="bi" :class="d.icon"></i></span>
            <div class="txt">
              <div class="t1">{{ d.title }}</div>
              <div class="t2">{{ d.username }} · {{ d.desc }}</div>
            </div>
            <i class="bi" :class="loadingUser === d.username ? 'bi-hourglass' : 'bi-arrow-up-right'"></i>
          </div>
        </div>
      </div>
      <footer>© 2026 志愿汇 · 社区志愿服务信息化平台</footer>
    </aside>

    <main class="auth-main">
      <div class="auth-card">
        <div class="eyebrow">Welcome back</div>
        <h1>登录你的志愿账户</h1>
        <div class="desc">使用体验账号或自行注册，登录后按角色进入不同的工作台。</div>

        <div class="form-box">
          <form @submit.prevent="onSubmit">
            <div class="field">
              <label class="label">用户名</label>
              <input v-model.trim="form.username" class="input" placeholder="请输入用户名" autocomplete="username" />
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
            <button class="btn btn-primary btn-block" :disabled="loading" style="margin-top: 8px">
              <i class="bi bi-box-arrow-in-right"></i>
              {{ loading ? '登录中…' : '登 录' }}
            </button>
          </form>
        </div>

        <div class="auth-foot">
          还没有账号？<router-link to="/register">注册成为志愿者</router-link>
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

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const loadingUser = ref('')

const demos = [
  {
    username: 'volunteer01',
    password: '123456',
    title: '志愿者视角',
    desc: '浏览活动 · 报名 · 查看工时',
    icon: 'bi-person-heart'
  },
  {
    username: 'organizer01',
    password: '123456',
    title: '组织者视角',
    desc: '发布活动 · 审核报名 · 发工时',
    icon: 'bi-megaphone'
  },
  {
    username: 'admin',
    password: '123456',
    title: '管理员视角',
    desc: '全站数据看板与活动管理',
    icon: 'bi-shield-check'
  }
]

async function signIn(username, password) {
  const res = await api.login({ username, password })
  auth.setSession(res.token, { id: res.id, name: res.name, role: res.role })
  toast.success(`欢迎回来，${res.name}`)
  router.push('/activities')
}

async function onSubmit() {
  if (!form.username || !form.password) return toast.error('请填写用户名和密码')
  loading.value = true
  try {
    await signIn(form.username, form.password)
  } catch (e) {
    toast.error(e.message)
  } finally {
    loading.value = false
  }
}

async function quickLogin(d) {
  if (loadingUser.value) return
  loadingUser.value = d.username
  try {
    await signIn(d.username, d.password)
  } catch (e) {
    toast.error(e.message + '（请确认已执行初始化数据脚本）')
  } finally {
    loadingUser.value = ''
  }
}
</script>
