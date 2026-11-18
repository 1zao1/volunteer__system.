import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'

const routes = [
  { path: '/', redirect: '/activities' },
  { path: '/login', component: () => import('../views/Login.vue'), meta: { guest: true } },
  { path: '/register', component: () => import('../views/Register.vue'), meta: { guest: true } },
  { path: '/activities', component: () => import('../views/ActivityList.vue') },
  { path: '/activity/:id', component: () => import('../views/ActivityDetail.vue') },
  { path: '/my-registrations', component: () => import('../views/MyRegistrations.vue') },
  { path: '/my-hours', component: () => import('../views/MyHours.vue') },
  { path: '/admin', component: () => import('../views/AdminDashboard.vue'), meta: { roles: ['admin', 'organizer'] } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.guest && !auth.isLogin) return '/login'
  if (to.meta.roles && !to.meta.roles.includes(auth.role)) return '/activities'
  if (to.meta.guest && auth.isLogin) return '/activities'
})

export default router