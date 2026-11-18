<template>
  <NavBar v-if="auth.isLogin" />
  <div class="container py-4">
    <router-view />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
import { useAuthStore } from './store/auth'

const auth = useAuthStore()
</script>