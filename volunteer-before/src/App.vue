<template>
  <div>
    <NavBar v-if="auth.isLogin" />
    <main :class="auth.isLogin ? 'page' : ''">
      <div class="container">
        <router-view />
      </div>
    </main>
    <Toast />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
import Toast from './components/Toast.vue'
import { useAuthStore } from './store/auth'

const auth = useAuthStore()
</script>
