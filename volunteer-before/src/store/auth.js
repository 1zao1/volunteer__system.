import { defineStore } from 'pinia'

const ROLE_MAP = {
  volunteer: { name: '志愿者', icon: 'bi-person-heart' },
  organizer: { name: '组织者', icon: 'bi-megaphone' },
  admin: { name: '管理员', icon: 'bi-shield-check' }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),
  getters: {
    isLogin: (s) => !!s.token,
    role: (s) => s.user?.role || '',
    roleName: (s) => ROLE_MAP[s.user?.role]?.name || '',
    roleIcon: (s) => ROLE_MAP[s.user?.role]?.icon || 'bi-person',
    isAdmin: (s) => ['admin', 'organizer'].includes(s.user?.role),
    initial: (s) => (s.user?.name || '?').slice(0, 1).toUpperCase()
  },
  actions: {
    setSession(token, user) {
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
