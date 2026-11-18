<template>
  <div class="auth">
    <aside class="auth-side">
      <div>
        <div class="brand">
          <span class="logo-mark"><i class="bi bi-heart-pulse"></i></span>
          <span>予光 · YuGuang</span>
        </div>
        <h2>加入我们，<br />把<em>善意</em>变成日常</h2>
        <p>注册后即可报名全市各类志愿活动，服务时长自动累计，可用于评优与证明开具。</p>

        <div class="demo-list">
          <div class="demo-label">注 册 后 可 以 做 什 么</div>
          <div class="demo" style="cursor: default">
            <span class="ico"><i class="bi bi-compass"></i></span>
            <div class="txt">
              <div class="t1">浏览与报名活动</div>
              <div class="t2">按分类筛选，实时查看余位</div>
            </div>
          </div>
          <div class="demo" style="cursor: default">
            <span class="ico"><i class="bi bi-clock-history"></i></span>
            <div class="txt">
              <div class="t1">累计服务工时</div>
              <div class="t2">每次服务结束后由组织者认定</div>
            </div>
          </div>
          <div class="demo" style="cursor: default">
            <span class="ico"><i class="bi bi-patch-check"></i></span>
            <div class="txt">
              <div class="t1">生成服务记录</div>
              <div class="t2">报名与工时全程留痕，可追溯</div>
            </div>
          </div>
        </div>
      </div>
      <footer>© 2026 予光 · 社区志愿服务信息化平台</footer>
    </aside>

    <main class="auth-main">
      <div class="auth-card">
        <div class="eyebrow">Join us</div>
        <h1>注册志愿者账号</h1>
        <div class="desc">填写基本信息即可完成注册，默认角色为志愿者。</div>

        <div class="form-box">
          <form @submit.prevent="onSubmit">
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
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { toast } from '../utils/toast'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', phone: '', password: '', skills: '' })

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
