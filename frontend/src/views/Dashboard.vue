<template>
  <div class="flex h-screen bg-gray-100">
    <!-- Sidebar -->
    <aside class="w-64 bg-white shadow-md">
      <div class="p-4">
        <h1 class="text-xl font-bold text-gray-800">NISIRCOP-LE</h1>
      </div>
      <nav class="mt-5">
        <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-200">Dashboard</a>
        <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-200">Incidents</a>
        <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-200">Analytics</a>
        <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-200">Users</a>
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
          <button @click="showIncidentForm = true" class="px-4 py-2 mr-4 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
            Report Incident
          </button>
          <span class="mr-4 text-sm text-gray-600">Welcome, {{ authStore.user?.username }}</span>
          <button @click="handleLogout" class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700">
            Logout
          </button>
        </div>
      </header>

      <!-- Map Area -->
      <main class="relative flex-1 overflow-x-hidden overflow-y-auto bg-gray-200">
        <div class="h-full" id="map-container">
          <!-- Leaflet map will be mounted here -->
        </div>
        <!-- Incident Form Modal -->
        <div v-if="showIncidentForm" class="absolute inset-0 z-10 flex items-center justify-center bg-black bg-opacity-50">
          <IncidentForm @close="showIncidentForm = false" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/auth.store';
import { useRouter } from 'vue-router';
import { onMounted, ref } from 'vue';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import IncidentForm from '../components/IncidentForm.vue';

const authStore = useAuthStore();
const router = useRouter();
const showIncidentForm = ref(false);

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

onMounted(() => {
  // Initialize map
  const map = L.map('map-container').setView([9.145, 38.7667], 13); // Addis Ababa
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }).addTo(map);
});
</script>

<style>
#map-container {
  height: 100%;
  width: 100%;
}
</style>