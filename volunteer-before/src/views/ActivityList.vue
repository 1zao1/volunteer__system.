<template>
  <section class="hero">
    <div class="hero-badge"><i class="bi bi-stars"></i> 承德市社区志愿服务平台</div>
    <h1>让善意流动，<br />让每一次<em>付出</em>都有回响</h1>
    <p>从社区义诊到赛事保障，挑一个与你时间、技能都合拍的活动，报名只需一步。</p>

    <div class="search">
      <i class="bi bi-search"></i>
      <input v-model.trim="keyword" placeholder="搜索活动名称，如：义诊、马拉松、支教…" />
      <button v-if="keyword" class="btn btn-ghost btn-sm" @click="keyword = ''">
        <i class="bi bi-x-lg"></i>
      </button>
    </div>

    <div class="stats-strip">
      <div class="item">
        <div class="num">{{ overview.activityCount ?? '—' }}<small>场</small></div>
        <div class="label">正在招募的活动</div>
      </div>
      <div class="item">
        <div class="num">{{ overview.volunteerCount ?? '—' }}<small>人</small></div>
        <div class="label">注册志愿者</div>
      </div>
      <div class="item">
        <div class="num">{{ overview.hoursTotal ?? '—' }}<small>h</small></div>
        <div class="label">累计认定工时</div>
      </div>
      <div class="item">
        <div class="num">{{ overview.signupCount ?? '—' }}<small>次</small></div>
        <div class="label">累计报名人次</div>
      </div>
    </div>
  </section>

  <div class="chips">
    <button class="chip" :class="{ active: category === '全部' }" @click="category = '全部'">
      全部 <span class="count">{{ list.length }}</span>
    </button>
    <button
      v-for="c in categories"
      :key="c.name"
      class="chip"
      :class="{ active: category === c.name }"
      @click="category = c.name"
    >
      {{ c.name }} <span class="count">{{ c.count }}</span>
    </button>
  </div>

  <div v-if="err" class="panel" style="display: flex; gap: 14px; align-items: center; padding: 20px 22px">
    <i class="bi bi-exclamation-triangle-fill" style="font-size: 22px; color: #c2410c"></i>
    <div style="flex: 1">
      <div class="strong">活动数据加载失败</div>
      <div class="muted" style="font-size: 13px; margin-top: 2px">{{ err }}</div>
    </div>
    <button class="btn btn-primary btn-sm" @click="load">重新加载</button>
  </div>

  <div class="cards-grid" v-else-if="filtered.length">
    <ActivityCard v-for="a in filtered" :key="a.id" :a="a" @signup="signup" />
  </div>

  <div v-else-if="loading" class="cards-grid">
    <div v-for="i in 3" :key="i" class="card" style="height: 330px">
      <div class="skeleton" style="height: 100%"></div>
    </div>
  </div>

  <EmptyState
    v-else
    icon="bi-calendar-x"
    title="没有匹配的活动"
    desc="换个关键词或分类试试，新的活动每周都会上架。"
  />
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import ActivityCard from '../components/ActivityCard.vue'
import EmptyState from '../components/EmptyState.vue'
import { toast } from '../utils/toast'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const list = ref([])
const overview = ref({})
const keyword = ref('')
const category = ref('全部')
const loading = ref(true)
const err = ref('')

const categories = computed(() => {
  const map = new Map()
  list.value.forEach((a) => map.set(a.category, (map.get(a.category) || 0) + 1))
  return [...map.entries()].map(([name, count]) => ({ name, count }))
})

const filtered = computed(() =>
  list.value.filter((a) => {
    const okCat = category.value === '全部' || a.category === category.value
    const okKey =
      !keyword.value ||
      (a.title || '').includes(keyword.value) ||
      (a.location || '').includes(keyword.value)
    return okCat && okKey
  })
)

async function load() {
  loading.value = true
  err.value = ''
  try {
    const [acts, ov] = await Promise.all([api.getActivities(), api.getOverview()])
    list.value = Array.isArray(acts) ? acts : acts?.data || []
    overview.value = ov || {}
  } catch (e) {
    err.value = e.message
    toast.error(e.message)
  } finally {
    loading.value = false
  }
}

async function signup(a) {
  if (auth.role === 'admin') return toast.error('管理员请到「活动管理」中操作')
  if (auth.role === 'organizer' && a.organizerId === auth.user?.id) {
    return toast.error('这是你发布的活动，不能报名自己的活动')
  }
  const credit = auth.creditScore
  if (credit < 80) {
    return toast.error(`你当前信誉分为 ${credit} 分，暂不能报名新活动，每 2 小时会恢复 1 分`)
  }
  if (credit <= 85) {
    const ok = window.confirm(`您当前信誉分为${credit}分，若再次违约你就会成为“低信誉志愿者”。确定继续报名吗？`)
    if (!ok) return
  }
  try {
    await api.signup(a.id)
    toast.success('报名成功，等待组织者审核')
    await load()
  } catch (e) {
    toast.error(e.message)
  }
}

onMounted(load)
</script>
