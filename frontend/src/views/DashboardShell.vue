<template>
  <div class="flex h-screen bg-[#021024] text-[#C1E8FF]">
    <!-- Sidebar -->
    <aside class="w-64 flex-shrink-0 bg-[#052659] p-4 flex flex-col hidden lg:flex">
      <div class="text-center py-4">
        <h1 class="text-2xl font-extrabold tracking-wider">NISIRCOP-LE</h1>
      </div>
      <nav class="mt-8 flex-1">
        <router-link
          v-for="item in navigation"
          :key="item.name"
          :to="item.href"
          v-show="isNavAllowed(item)"
          :class="[
            'flex items-center px-4 py-3 my-2 text-md font-medium rounded-lg transition-colors duration-200',
            'hover:bg-[#5483B3] hover:text-white',
          ]"
          active-class="bg-[#5483B3] text-white"
        >
          <component :is="item.icon" class="h-6 w-6 mr-3" aria-hidden="true" />
          {{ item.name }}
        </router-link>
      </nav>
      <div class="mt-auto">
        <button
          @click="handleLogout"
          class="w-full flex items-center px-4 py-3 text-md font-medium rounded-lg transition-colors duration-200 hover:bg-[#5483B3] hover:text-white"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
          Logout
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Header -->
      <header class="flex items-center justify-between p-4 bg-[#052659] border-b border-[#5483B3]">
        <div class="flex items-center">
          <button @click="toggleMobileMenu" class="lg:hidden mr-4 p-2 rounded-md hover:bg-[#5483B3]">
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <h2 class="text-xl font-semibold">Welcome, {{ authStore.user?.username }}</h2>
        </div>
        <div class="flex items-center">
          <button
            v-if="canReportIncident"
            @click="uiStore.openReportIncidentModal()"
            class="px-4 py-2 text-sm font-medium text-white bg-[#5483B3] rounded-md hover:bg-[#7DA0CA] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#7DA0CA] transition-colors"
          >
            Report Incident
          </button>
        </div>
      </header>

      <!-- Mobile Menu -->
      <div v-if="showMobileMenu" class="lg:hidden bg-[#052659] border-b border-[#5483B3]">
        <nav class="p-4">
          <router-link
            v-for="item in navigation"
            :key="item.name"
            :to="item.href"
            v-show="isNavAllowed(item)"
            @click="showMobileMenu = false"
            :class="[
              'flex items-center px-4 py-3 my-2 text-md font-medium rounded-lg transition-colors duration-200',
              'hover:bg-[#5483B3] hover:text-white',
            ]"
            active-class="bg-[#5483B3] text-white"
          >
            <component :is="item.icon" class="h-6 w-6 mr-3" aria-hidden="true" />
            {{ item.name }}
          </router-link>
          <button
            @click="handleLogout"
            class="w-full flex items-center px-4 py-3 text-md font-medium rounded-lg transition-colors duration-200 hover:bg-[#5483B3] hover:text-white"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
            Logout
          </button>
        </nav>
      </div>

      <!-- Content Area -->
      <main class="flex-1 overflow-x-hidden overflow-y-auto bg-[#021024] p-6">
        <router-view />
      </main>
    </div>

    <!-- Report Incident Modal -->
    <ReportIncidentModal
      :is-open="isReportModalOpen"
      :latitude="modalLatitude"
      :longitude="modalLongitude"
      @close="uiStore.closeReportIncidentModal"
    />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/auth.store'
import { useUiStore } from '../stores/ui.store'
import { useRouter } from 'vue-router'
import { shallowRef, computed } from 'vue'
import ReportIncidentModal from '../components/ReportIncidentModal.vue'

// Icons
const HomeIcon = shallowRef({ template: `<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-7-4h6" /></svg>`})
const DocumentReportIcon = shallowRef({ template: `<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>`})
const ChartBarIcon = shallowRef({ template: `<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" /></svg>`})
const UsersIcon = shallowRef({ template: `<svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M15 21a6 6 0 00-9-5.197M15 21a6 6 0 00-9-5.197" /></svg>`})

const authStore = useAuthStore()
const uiStore = useUiStore()
const router = useRouter()
const showMobileMenu = ref(false)

const navigation = [
  { name: 'Dashboard', href: '/', icon: HomeIcon, allowedRoles: ['SUPER_USER', 'POLICE_STATION', 'OFFICER'] },
  { name: 'Incidents', href: '/incidents', icon: DocumentReportIcon, allowedRoles: ['SUPER_USER', 'POLICE_STATION', 'OFFICER'] },
  { name: 'Analytics', href: '/analytics', icon: ChartBarIcon, allowedRoles: ['SUPER_USER', 'POLICE_STATION', 'OFFICER'] },
  { name: 'Users', href: '/users', icon: UsersIcon, allowedRoles: ['SUPER_USER', 'POLICE_STATION'] },
]

const isReportModalOpen = computed(() => uiStore.reportIncidentModal.isOpen);
const modalLatitude = computed(() => uiStore.reportIncidentModal.lat);
const modalLongitude = computed(() => uiStore.reportIncidentModal.lng);

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const isNavAllowed = (navItem: typeof navigation[0]) => {
  if (!authStore.user?.role) return false;
  return navItem.allowedRoles.includes(authStore.user.role);
};

const canReportIncident = computed(() => {
  if (!authStore.user?.role) return false;
  return ['SUPER_USER', 'OFFICER'].includes(authStore.user.role);
});

</script>

<style scoped>
.router-link-exact-active {
  background-color: #5483B3;
  color: white;
}
</style>