<template>
  <router-link to="/activities" class="btn btn-link ps-0"><i class="bi bi-arrow-left"></i> 返回列表</router-link>
  <div class="card mt-2" v-if="a">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-start">
        <h3>{{ a.title }}</h3>
        <span class="badge bg-info text-dark">{{ a.category }}</span>
      </div>
      <hr>
      <p><i class="bi bi-geo-alt text-success"></i> 地点：{{ a.location }}</p>
      <p><i class="bi bi-clock text-success"></i> 时间：{{ a.startTime }} ~ {{ a.endTime }}</p>
      <p><i class="bi bi-hourglass text-success"></i> 工时：{{ a.hours }} 小时</p>
      <p><i class="bi bi-people text-success"></i> 人数：{{ a.signedCount }}/{{ a.maxCount }}</p>
      <hr>
      <p>{{ a.description }}</p>
      <button class="btn btn-success" @click="signup" :disabled="a.signedCount >= a.maxCount">
        {{ a.signedCount >= a.maxCount ? '已满员' : '立即报名' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const a = ref(null)

onMounted(async () => {
  a.value = await api.getActivity(route.params.id)
})

async function signup() {
  try {
    await api.signup(a.value.id)
    alert('报名成功')
    a.value = await api.getActivity(route.params.id)
  } catch (e) {
    alert(e.message)
  }
}
</script>