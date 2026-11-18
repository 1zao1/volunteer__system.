<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-success">
    <div class="container">
      <router-link class="navbar-brand" to="/activities">
        <i class="bi bi-heart-pulse"></i> 志愿汇
      </router-link>
      <button class="navbar-toggler" data-bs-toggle="collapse" data-bs-target="#navMenu">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navMenu">
        <ul class="navbar-nav me-auto">
          <li v-for="m in menus" :key="m.path" class="nav-item">
            <router-link class="nav-link" :to="m.path">
              <i class="bi" :class="m.icon"></i> {{ m.text }}
            </router-link>
          </li>
        </ul>
        <span class="text-white me-3">👤 {{ auth.user?.name }}（{{ auth.roleName }}）</span>
        <button class="btn btn-outline-light btn-sm" @click="logout">退出</button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const router = useRouter()

const all = [
  { path: '/activities', text: '活动广场', icon: 'bi-list' },
  { path: '/my-registrations', text: '我的报名', icon: 'bi-journal-check', roles: ['volunteer', 'organizer'] },
  { path: '/my-hours', text: '我的工时', icon: 'bi-clock-history', roles: ['volunteer', 'organizer'] },
  { path: '/admin', text: '后台看板', icon: 'bi-speedometer2', roles: ['admin', 'organizer'] }
]

const menus = computed(() => all.filter(m => !m.roles || m.roles.includes(auth.role)))

function logout() {
  auth.logout()
  router.push('/login')
}
</script>