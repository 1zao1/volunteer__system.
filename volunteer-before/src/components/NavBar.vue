<template>
  <header class="nav">
    <div class="container nav-inner">
      <router-link to="/activities" class="logo">
        <span class="logo-mark"><i class="bi bi-heart-pulse"></i></span>
        <span class="logo-text">志愿<span>汇</span></span>
      </router-link>

      <nav class="nav-links">
        <router-link
          v-for="m in menus"
          :key="m.path"
          :to="m.path"
          class="nav-link"
          :class="{ active: isActive(m.path) }"
        >
          <i class="bi" :class="m.icon"></i>{{ m.text }}
        </router-link>
      </nav>

      <div class="nav-right">
        <span class="role-pill">
          <i class="bi" :class="auth.roleIcon"></i>{{ auth.roleName }}
        </span>
        <div class="user-chip">
          <span class="avatar">{{ auth.initial }}</span>
          <span class="user-name">{{ auth.user?.name }}</span>
        </div>
        <button class="btn btn-ghost btn-sm" @click="logout">
          <i class="bi bi-box-arrow-right"></i>退出
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const all = [
  { path: '/activities', text: '活动广场', icon: 'bi-compass' },
  { path: '/my-registrations', text: '我的报名', icon: 'bi-journal-check', roles: ['volunteer'] },
  { path: '/my-hours', text: '我的工时', icon: 'bi-clock-history', roles: ['volunteer'] },
  { path: '/dashboard', text: '数据看板', icon: 'bi-speedometer2', roles: ['admin', 'organizer'] },
  { path: '/admin/activities', text: '活动管理', icon: 'bi-calendar2-week', roles: ['admin', 'organizer'] },
  { path: '/admin/audit', text: '报名审核', icon: 'bi-clipboard-check', roles: ['admin', 'organizer'] },
  { path: '/admin/users', text: '用户管理', icon: 'bi-people', roles: ['admin'] }
]

const menus = computed(() => all.filter((m) => !m.roles || m.roles.includes(auth.role)))

function isActive(path) {
  return route.path === path || (path === '/activities' && route.path.startsWith('/activity/'))
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>
