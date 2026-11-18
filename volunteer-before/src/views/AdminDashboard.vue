<template>
  <div class="section-title">
    <div>
      <h2>{{ isAdmin ? '全站治理看板' : '我的组织工作台' }}</h2>
      <div class="sub">
        {{ isAdmin
          ? '管理员关注平台整体运行与志愿者信誉，不参与报名审核'
          : '你只能审核自己发布活动的报名，其他组织者的活动由其本人审核' }}
      </div>
    </div>
    <button class="btn btn-ghost btn-sm" @click="load">
      <i class="bi bi-arrow-clockwise"></i> 刷新数据
    </button>
  </div>

  <!-- 管理员视角 -->
  <template v-if="isAdmin">
    <div class="stats-grid five">
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
        <div class="icon sky"><i class="bi bi-megaphone"></i></div>
        <div>
          <div class="num">{{ d.organizers ?? 0 }}</div>
          <div class="label">注册组织者</div>
        </div>
      </div>
      <div class="stat">
        <div class="icon amber"><i class="bi bi-hourglass-split"></i></div>
        <div>
          <div class="num">{{ d.totalHours ?? 0 }}</div>
          <div class="label">全站已认定工时（小时）</div>
        </div>
      </div>
      <div class="stat" style="cursor: pointer" @click="$router.push('/admin/users')">
        <div class="icon rose"><i class="bi bi-shield-exclamation"></i></div>
        <div>
          <div class="num">{{ d.lowCredit ?? 0 }}</div>
          <div class="label">低信誉账号（&lt;85 分）</div>
        </div>
      </div>
    </div>

    <div class="two-col mt-24">
      <div class="panel">
        <div class="panel-head">
          <h3>信誉分分布</h3>
          <span class="sub">违约扣 5 分，每 2 小时恢复 1 分（上限 85）</span>
        </div>
        <div class="bar-list">
          <div v-for="c in d.creditDist || []" :key="c.name" class="bar-row credit-row">
            <span class="bar-label">{{ c.name }}</span>
            <span class="bar-track">
              <span class="bar-fill" :style="{ width: creditWidth(c.count) }" :class="{ low: c.name !== '95-100' }"></span>
            </span>
            <span class="bar-val">{{ c.count }}</span>
          </div>
          <div v-if="!(d.creditDist || []).length" class="muted">暂无数据</div>
        </div>
        <div class="hint">低于 85 分即为低信誉，报名时会收到提示；每 2 小时自动恢复 1 分，上限 85 分。组织者与志愿者同样适用。</div>
      </div>

      <div class="panel">
        <div class="panel-head">
          <h3>低信誉名单</h3>
          <span class="sub">按分数从低到高</span>
        </div>
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr><th>账号</th><th>角色</th><th style="text-align: right">信誉分</th></tr>
            </thead>
            <tbody>
              <tr v-for="u in d.lowCreditUsers || []" :key="u.id">
                <td class="strong">{{ u.nickname || u.username }}</td>
                <td class="muted">{{ roleText(u.role) }}</td>
                <td style="text-align: right">
                  <span class="pill" :class="u.creditScore < 80 ? 'rose' : 'amber'">{{ u.creditScore }}</span>
                </td>
              </tr>
              <tr v-if="!(d.lowCreditUsers || []).length">
                <td colspan="3" class="muted" style="text-align: center; padding: 24px">暂无低信誉志愿者</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </template>

  <!-- 组织者视角 -->
  <template v-else>
    <div class="stats-grid">
      <div class="stat">
        <div class="icon green"><i class="bi bi-calendar2-week"></i></div>
        <div>
          <div class="num">{{ d.totalActivities ?? 0 }}</div>
          <div class="label">我发布的活动（{{ d.publishedActivities ?? 0 }} 场招募中）</div>
        </div>
      </div>
      <div class="stat" style="cursor: pointer" @click="$router.push('/admin/audit')">
        <div class="icon amber"><i class="bi bi-clipboard-check"></i></div>
        <div>
          <div class="num">{{ d.pendingAudit ?? 0 }}</div>
          <div class="label">待我审核的报名</div>
        </div>
      </div>
      <div class="stat">
        <div class="icon sky"><i class="bi bi-hourglass-split"></i></div>
        <div>
          <div class="num">{{ d.pendingHours ?? 0 }}</div>
          <div class="label">待认定工时</div>
        </div>
      </div>
      <div class="stat">
        <div class="icon rose"><i class="bi bi-clock-history"></i></div>
        <div>
          <div class="num">{{ d.totalHours ?? 0 }}</div>
          <div class="label">已认定总工时（小时）</div>
        </div>
      </div>
    </div>

    <div class="two-col mt-16" style="grid-template-columns: 1fr 1fr">
      <div class="card list-card" style="padding: 16px 18px">
        <span class="bar"></span>
        <div class="main">
          <div class="t" style="font-size: 14px">待审核报名</div>
          <div class="s">{{ d.pendingAudit ?? 0 }} 条等待你处理</div>
        </div>
        <router-link to="/admin/audit" class="btn btn-soft btn-sm">去审核</router-link>
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
  </template>

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

  <div class="panel mt-24">
    <div class="panel-head">
      <h3>最近报名</h3>
      <span class="sub">{{ isAdmin ? '仅展示，审核由各活动组织者完成' : '你活动的报名记录' }}</span>
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
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import { fmtTime, STATUS_TEXT } from '../utils/category'
import { toast } from '../utils/toast'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const d = ref({})
const isAdmin = computed(() => auth.role === 'admin')

function statusOf(s) {
  return STATUS_TEXT[s] || { text: s, cls: 'grey' }
}

function roleText(r) {
  return { volunteer: '志愿者', organizer: '组织者', admin: '管理员' }[r] || r
}

const maxCat = computed(() => Math.max(1, ...(d.value.categoryStats || []).map((c) => c.count)))
function barWidth(n) {
  return `${Math.round((n / maxCat.value) * 100)}%`
}

const maxCredit = computed(() => Math.max(1, ...(d.value.creditDist || []).map((c) => c.count)))
function creditWidth(n) {
  return `${Math.max(2, Math.round((n / maxCredit.value) * 100))}%`
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
