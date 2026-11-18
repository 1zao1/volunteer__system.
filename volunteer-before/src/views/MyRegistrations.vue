<template>
  <h4 class="mb-4"><i class="bi bi-journal-check"></i> 我的报名</h4>
  <table class="table table-hover bg-white">
    <thead class="table-success">
      <tr><th>活动名称</th><th>报名时间</th><th>状态</th></tr>
    </thead>
    <tbody>
      <tr v-for="r in list" :key="r.id">
        <td>{{ r.activityTitle }}</td>
        <td>{{ r.signupTime }}</td>
        <td>
          <span class="badge" :class="r.status === 'pending' ? 'bg-warning text-dark' : 'bg-success'">
            {{ r.status === 'pending' ? '待审核' : '已通过' }}
          </span>
        </td>
      </tr>
      <tr v-if="list.length === 0">
        <td colspan="3" class="text-center text-muted">暂无报名记录</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'

const list = ref([])

onMounted(async () => {
  list.value = await api.getMyRegistrations()
})
</script>