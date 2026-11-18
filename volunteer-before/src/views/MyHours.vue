<template>
  <h4 class="mb-4"><i class="bi bi-clock-history"></i> 我的工时</h4>
  <div class="alert alert-success">
    累计工时：<strong>{{ total }}</strong> 小时
  </div>
  <table class="table table-hover bg-white">
    <thead class="table-success">
      <tr><th>活动</th><th>日期</th><th>工时</th></tr>
    </thead>
    <tbody>
      <tr v-for="(x, i) in list" :key="i">
        <td>{{ x.activityTitle }}</td>
        <td>{{ x.date }}</td>
        <td><span class="badge bg-success">{{ x.hours }} h</span></td>
      </tr>
      <tr v-if="list.length === 0">
        <td colspan="3" class="text-center text-muted">暂无工时记录</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'

const list = ref([])
const total = computed(() => list.value.reduce((s, x) => s + x.hours, 0))

onMounted(async () => {
  list.value = await api.getMyHours()
})
</script>