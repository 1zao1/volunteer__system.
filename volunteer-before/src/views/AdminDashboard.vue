<template>
  <div class="section-title">
    <div>
      <h2>数据看板</h2>
      <div class="sub">
        {{ auth.role === 'admin' ? '管理员视角：全站活动、用户、工时与审核总览' : '组织者视角：你所运营的活动与报名处理情况' }}
      </div>
    </div>
    <button class="btn btn-ghost btn-sm" @click="load">
      <i class="bi bi-arrow-clockwise"></i> 刷新数据
    </button>
  </div>

  <div class="stats-grid">
    <div class="stat">
      <div class="icon green"><i class="bi bi-calendar2-week"></i></div>
      <div>
        <div class="num">{{ d.totalActivities ?? 0 }}</div>
        <div class="label">活动总数（{{ d.publishedActivities ?? 0 }} 场招募中）</div>
      </div>
    </div>
    <div class="stat">
      <div class="icon sky"><i class="bi bi-people"></i></div>
      <div>
        <div class="num">{{ d.totalVolunteers ?? 0 }}</div>
        <div class="label">注册志愿者</div>
      </div>
    </div>
    <div class="stat">
      <div class="icon amber"><i class="bi bi-hourglass-split"></i></div>
      <div>
        <div class="num">{{ d.totalHours ?? 0 }}</div>
        <div class="label">累计认定工时（小时）</div>
      </div>
    </div>
    <div class="stat" style="cursor: pointer" @click="$router.push('/admin/audit')">
      <div class="icon rose"><i class="bi bi-clipboard-check"></i></div>
      <div>
        <div class="num">{{ d.pendingAudit ?? 0 }}</div>
        <div class="label">待审核报名（点击去处理）</div>
      </div>
    </div>
  </div>

  <div
    v-if="auth.role === 'admin'"
    class="panel mt-16"
    style="display: flex; gap: 14px; align-items: center; padding: 14px 20px"
  >
    <span class="strong" style="font-size: 13px">角色构成</span>
    <span class="pill green">志愿者 {{ d.totalVolunteers ?? 0 }}</span>
    <span class="pill amber">组织者 {{ d.organizers ?? 0 }}</span>
    <span class="pill grey">管理员 {{ d.admins ?? 0 }}</span>
    <router-link to="/admin/users" class="btn btn-ghost btn-sm" style="margin-left: auto">
      进入用户管理 <i class="bi bi-arrow-right"></i>
    </router-link>
  </div>

  <div class="two-col mt-24">
    <div class="panel">
      <div class="panel-head">
        <h3>活动分类分布</h3>
        <span class="sub">按活动数量统计</span>
      </div>
      <div class="bar-list">
        <div v-for="c in d.categoryStats || []" :key="c.name" class="bar-row">
          <span class="bar-label">{{ c.name }}</span>
          <span class="bar-track">
            <span class="bar-fill" :style="{ width: barWidth(c.count) }"></span>
          </span>
          <span class="bar-val">{{ c.count }}</span>
        </div>
        <div v-if="!(d.categoryStats || []).length" class="muted">暂无数据</div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>近 7 天报名趋势</h3>
        <span class="sub">共 {{ trendTotal }} 人次</span>
      </div>
      <div class="trend">
        <div v-for="t in d.trend || []" :key="t.date" class="trend-col">
          <span class="muted" style="font-size: 11.5px">{{ t.count }}</span>
          <span class="trend-bar" :style="{ height: trendHeight(t.count) }"></span>
          <span class="trend-day">{{ t.date }}</span>
        </div>
      </div>
    </div>
  </div>

  <div class="two-col mt-24" style="grid-template-columns: 1fr 360px">
    <div class="panel">
      <div class="panel-head">
        <h3>最近报名</h3>
        <router-link to="/admin/audit" class="btn btn-ghost btn-sm">查看全部</router-link>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr><th>志愿者</th><th>活动</th><th>报名时间</th><th>状态</th></tr>
          </thead>
          <tbody>
            <tr v-for="r in d.recent || []" :key="r.id">
              <td class="strong">{{ r.username }}</td>
              <td>{{ r.activityTitle }}</td>
              <td class="muted">{{ fmtTime(r.signupTime) }}</td>
              <td><span class="pill" :class="statusOf(r.status).cls">{{ statusOf(r.status).text }}</span></td>
            </tr>
            <tr v-if="!(d.recent || []).length">
              <td colspan="4" class="muted" style="text-align: center; padding: 28px">暂无报名记录</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head"><h3>待办事项</h3></div>
      <div class="list-stack">
        <div class="card list-card" style="padding: 16px 18px">
          <span class="bar"></span>
          <div class="main">
            <div class="t" style="font-size: 14px">待审核报名</div>
            <div class="s">{{ d.pendingAudit ?? 0 }} 条等待处理</div>
          </div>
          <router-link to="/admin/audit" class="btn btn-soft btn-sm">处理</router-link>
        </div>
        <div class="card list-card" style="padding: 16px 18px">
          <span class="bar" style="background: linear-gradient(180deg, #f2a33c, #f7c777)"></span>
          <div class="main">
            <div class="t" style="font-size: 14px">待认定工时</div>
            <div class="s">{{ d.approvedUnissued ?? 0 }} 条已通过未发工时</div>
          </div>
          <router-link to="/admin/audit" class="btn btn-soft btn-sm">处理</router-link>
        </div>
        <div class="card list-card" style="padding: 16px 18px">
          <span class="bar" style="background: linear-gradient(180deg, #035e93, #38bdf8)"></span>
          <div class="main">
            <div class="t" style="font-size: 14px">发布新活动</div>
            <div class="s">当前 {{ d.publishedActivities ?? 0 }} 场在招募</div>
          </div>
          <router-link to="/admin/activities" class="btn btn-soft btn-sm">去发布</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import { fmtTime, STATUS_TEXT } from '../utils/category'
import { toast } from '../utils/toast'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const d = ref({})

function statusOf(s) {
  return STATUS_TEXT[s] || { text: s, cls: 'grey' }
}

const maxCat = computed(() => Math.max(1, ...(d.value.categoryStats || []).map((c) => c.count)))
function barWidth(n) {
  return `${Math.round((n / maxCat.value) * 100)}%`
}

const maxTrend = computed(() => Math.max(1, ...(d.value.trend || []).map((t) => t.count)))
const trendTotal = computed(() => (d.value.trend || []).reduce((s, t) => s + t.count, 0))
function trendHeight(n) {
  return `${Math.max(6, Math.round((n / maxTrend.value) * 100))}%`
}

async function load() {
  try {
    d.value = (await api.getDashboard()) || {}
  } catch (e) {
    toast.error(e.message)
  }
}

onMounted(load)
</script>
