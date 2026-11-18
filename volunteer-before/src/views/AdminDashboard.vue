<template>
  <h4 class="mb-4"><i class="bi bi-speedometer2"></i> 数据看板</h4>
  <div class="row g-3" v-if="d">
    <div class="col-md-3" v-for="c in cards" :key="c.label">
      <div class="stat-card p-4" :class="c.color">
        <i class="bi fs-3" :class="c.icon"></i>
        <h2 class="mt-2">{{ c.value }}</h2>
        <div>{{ c.label }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'

const d = ref(null)

onMounted(async () => {
  d.value = await api.getDashboard()
})

const cards = computed(() => d.value ? [
  { label: '活动总数', value: d.value.totalActivities, color: 'bg-primary', icon: 'bi-calendar-event' },
  { label: '志愿者人数', value: d.value.totalVolunteers, color: 'bg-success', icon: 'bi-people' },
  { label: '累计工时', value: d.value.totalHours, color: 'bg-warning', icon: 'bi-hourglass-split' },
  { label: '待审核', value: d.value.pendingAudit, color: 'bg-danger', icon: 'bi-clipboard-check' }
] : [])
</script>