<template>
  <div class="section-title">
    <div>
      <h2>我的报名</h2>
      <div class="sub">共 {{ list.length }} 条记录，审核通过后请按时到场</div>
    </div>
    <router-link to="/activities" class="btn btn-soft btn-sm">
      <i class="bi bi-plus-lg"></i> 再报一个活动
    </router-link>
  </div>

  <div class="stats-grid" style="grid-template-columns: repeat(3, 1fr); margin-bottom: 20px">
    <div class="stat">
      <div class="icon amber"><i class="bi bi-hourglass-split"></i></div>
      <div><div class="num">{{ counts.pending }}</div><div class="label">待审核</div></div>
    </div>
    <div class="stat">
      <div class="icon green"><i class="bi bi-check2-circle"></i></div>
      <div><div class="num">{{ counts.approved }}</div><div class="label">已通过</div></div>
    </div>
    <div class="stat">
      <div class="icon sky"><i class="bi bi-journal-check"></i></div>
      <div><div class="num">{{ list.length }}</div><div class="label">累计报名</div></div>
    </div>
  </div>

  <div class="list-stack" v-if="list.length">
    <div v-for="r in list" :key="r.id" class="card list-card">
      <span class="bar"></span>
      <div class="main">
        <div class="t">{{ r.activityTitle }}</div>
        <div class="s">
          <span><i class="bi bi-geo-alt"></i> {{ r.location || '—' }}</span>
          <span><i class="bi bi-calendar3"></i> {{ timeRange(r.startTime, r.endTime) }}</span>
          <span><i class="bi bi-clock-history"></i> 报名于 {{ fmtTime(r.signupTime) }}</span>
        </div>
      </div>
      <span class="pill" :class="statusOf(r.status).cls">{{ statusOf(r.status).text }}</span>
      <router-link
        v-if="r.activityId"
        :to="`/activity/${r.activityId}`"
        class="btn btn-ghost btn-sm"
      >
        查看活动
      </router-link>
      <span v-else class="muted" style="font-size: 12.5px">活动已下架</span>
      <button
        v-if="r.status === 'pending'"
        class="btn btn-danger btn-sm"
        @click="cancel(r)"
      >
        取消报名
      </button>
    </div>
  </div>

  <div v-else-if="loading" class="panel" style="height: 160px">
    <div class="skeleton" style="height: 100%"></div>
  </div>

  <EmptyState
    v-else
    icon="bi-journal-x"
    title="还没有报名记录"
    desc="去活动广场挑一个感兴趣的活动，报名只需一步。"
  >
    <router-link to="/activities" class="btn btn-primary btn-sm mt-16">去活动广场</router-link>
  </EmptyState>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import EmptyState from '../components/EmptyState.vue'
import { fmtTime, timeRange, STATUS_TEXT } from '../utils/category'
import { toast } from '../utils/toast'

const list = ref([])
const loading = ref(true)

const counts = computed(() => ({
  pending: list.value.filter((r) => r.status === 'pending').length,
  approved: list.value.filter((r) => r.status === 'approved').length
}))

function statusOf(s) {
  return STATUS_TEXT[s] || { text: s, cls: 'grey' }
}

async function load() {
  loading.value = true
  try {
    list.value = (await api.getMyRegistrations()) || []
  } catch (e) {
    toast.error(e.message)
  } finally {
    loading.value = false
  }
}

async function cancel(r) {
  if (!window.confirm(`确定取消「${r.activityTitle}」的报名吗？取消后名额将释放给其他志愿者。`)) return
  try {
    await api.cancelSignup(r.id)
    toast.success('已取消报名')
    await load()
  } catch (e) {
    toast.error(e.message)
  }
}

onMounted(load)
</script>
