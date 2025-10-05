<template>
  <div class="flex h-screen bg-gray-100">
    <!-- Sidebar -->
  <aside class="w-64 bg-[#f0f0f0] shadow-md">
      <div class="p-4">
        <h1 class="text-xl font-bold text-gray-800">NISIRCOP-LE</h1>
      </div>
      <nav class="mt-5">
  <router-link to="/" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Dashboard</router-link>
  <router-link to="/incidents" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Incidents</router-link>
  <router-link to="/analytics" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Analytics</router-link>
  <router-link v-if="authStore.user?.role === 'ADMIN' || authStore.user?.role === 'SUPER_USER'" to="/users" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Users</router-link>
      </nav>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Header -->
      <header class="flex items-center justify-between p-4 bg-white border-b">
        <div class="flex items-center">
          <h2 class="text-lg font-semibold text-gray-800">Dashboard</h2>
        </div>
        <div class="flex items-center">
          <button @click="$emit('openReport')" class="px-4 py-2 mr-4 text-sm font-medium text-white bg-[#1478f0] rounded-md hover:bg-[#0f61c9]">
            Report Incident
          </button>
          <span class="mr-4 text-sm text-black">Welcome, {{ authStore.user?.username }}</span>
          <button @click="handleLogout" class="px-4 py-2 text-sm font-medium text-white bg-[#000000] rounded-md hover:bg-[#222222]">
            Logout
          </button>
        </div>
      </header>

      <!-- Map / Content Area: child routes will render here -->
      <main class="relative flex-1 overflow-x-hidden overflow-y-auto bg-gray-200">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/auth.store'
const authStore = useAuthStore()
import { useRouter } from 'vue-router'
const router = useRouter()
const handleLogout = () => { authStore.logout(); router.push('/login') }
</script>

<style scoped>
</style>
