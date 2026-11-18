<template>
  <h4 class="mb-4"><i class="bi bi-list-ul"></i> 活动广场</h4>
  <div class="row">
    <div class="col-md-6 col-lg-4 mb-4" v-for="a in activities" :key="a.id">
      <div class="card activity-card h-100">
        <div class="card-body">
          <div class="d-flex justify-content-between">
            <span class="badge bg-info text-dark">{{ a.category }}</span>
            <span class="badge" :class="a.signedCount >= a.maxCount ? 'bg-danger' : 'bg-success'">
              {{ a.signedCount >= a.maxCount ? '已满员' : `余位 ${a.maxCount - a.signedCount}` }}
            </span>
          </div>
          <h5 class="card-title mt-3">{{ a.title }}</h5>
          <p class="text-muted small mb-1"><i class="bi bi-geo-alt"></i> {{ a.location }}</p>
          <p class="text-muted small mb-1"><i class="bi bi-clock"></i> {{ a.startTime }} ~ {{ a.endTime }}</p>
          <p class="text-muted small"><i class="bi bi-hourglass"></i> 计 {{ a.hours }} 工时</p>
        </div>
        <div class="card-footer bg-white border-0 d-flex gap-2">
          <router-link :to="`/activity/${a.id}`" class="btn btn-outline-success btn-sm flex-fill">查看详情</router-link>
          <button class="btn btn-success btn-sm flex-fill" :disabled="a.signedCount >= a.maxCount" @click="signup(a.id)">报名</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'

const activities = ref([])

onMounted(async () => {
  activities.value = await api.getActivities()
})

async function signup(id) {
  try {
    await api.signup(id)
    alert('报名成功，等待审核')
    activities.value = await api.getActivities()
  } catch (e) {
    alert(e.message)
  }
}
</script>