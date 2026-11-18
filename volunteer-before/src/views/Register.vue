<template>
  <div class="auth">
    <aside class="auth-side">
      <div>
        <div class="brand">
          <span class="logo-mark"><i class="bi bi-stars"></i></span>
          <span>予光 · YuGuang</span>
        </div>
        <h2>加入我们，<br />把<em>善意</em>变成日常</h2>
        <p>注册时选择你的身份：志愿者报名参与，组织者发布活动并审核报名，管理员治理平台与信誉分。</p>

        <div class="demo-list">
          <div class="demo-label">不 同 身 份 可 以 做 什 么</div>
          <div class="demo" style="cursor: default">
            <span class="ico"><i class="bi bi-person-heart"></i></span>
            <div class="txt">
              <div class="t1">志愿者</div>
              <div class="t2">浏览报名 · 累计工时 · 信誉分</div>
            </div>
          </div>
          <div class="demo" style="cursor: default">
            <span class="ico"><i class="bi bi-megaphone"></i></span>
            <div class="txt">
              <div class="t1">组织者</div>
              <div class="t2">发布活动 · 审核自己活动的报名 · 认定工时</div>
            </div>
          </div>
          <div class="demo" style="cursor: default">
            <span class="ico"><i class="bi bi-shield-check"></i></span>
            <div class="txt">
              <div class="t1">管理员</div>
              <div class="t2">全站看板 · 用户与信誉分治理</div>
            </div>
          </div>
        </div>
      </div>
      <footer>© 2026 予光 · 社区志愿服务信息化平台</footer>
    </aside>

    <main class="auth-main">
      <div class="auth-card">
        <div class="eyebrow">Join us</div>
        <h1>{{ roleTitle }}</h1>
        <div class="desc">填写基本信息，并选择你要注册的身份。</div>

        <div class="form-box">
          <form @submit.prevent="onSubmit">
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
            <button class="btn btn-primary btn-block" :disabled="loading" style="margin-top: 8px">
              <i class="bi bi-person-plus"></i>
              {{ loading ? '提交中…' : '完成注册' }}
            </button>
          </form>
        </div>

        <div class="auth-foot">已有账号？<router-link to="/login">返回登录</router-link></div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { toast } from '../utils/toast'

const router = useRouter()
const loading = ref(false)
const form = reactive({ role: 'volunteer', username: '', phone: '', password: '', skills: '' })

const roles = [
  { key: 'volunteer', name: '志愿者', desc: '报名活动、累计工时', icon: 'bi-person-heart' },
  { key: 'organizer', name: '组织者', desc: '发布活动、审核报名', icon: 'bi-megaphone' },
  { key: 'admin', name: '管理员', desc: '看板与信誉治理', icon: 'bi-shield-check' }
]

const roleTitle = computed(() => {
  const r = roles.find((x) => x.key === form.role)
  return `注册${r ? r.name : '志愿者'}账号`
})

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
    toast.success(`注册成功，你已成为${roles.find((r) => r.key === form.role).name}，请登录`)
    router.push('/login')
  } catch (e) {
    toast.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>
