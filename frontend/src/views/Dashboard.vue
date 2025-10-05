<template>
  <div class="flex h-screen bg-gray-100">
    <!-- Sidebar -->
  <aside class="w-64 bg-[#f0f0f0] shadow-md">
      <div class="p-4">
  <h1 class="text-xl font-bold text-black">NISIRCOP-LE</h1>
      </div>
      <nav class="mt-5">
  <router-link to="/" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Dashboard</router-link>
  <router-link to="/incidents" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Incidents</router-link>
  <router-link to="/analytics" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Analytics</router-link>
  <router-link to="/users" class="block px-4 py-2 text-sm text-black hover:bg-[#dcf0f0]">Users</router-link>
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
          <button v-if="authStore.user?.role !== 'ADMIN'" @click="openReport" class="px-4 py-2 mr-4 text-sm font-medium text-white bg-[#1478f0] rounded-md hover:bg-[#0f61c9]">
            Report Incident
          </button>
          <span class="mr-4 text-sm text-black">Welcome, {{ authStore.user?.username }}</span>
          <button @click="handleLogout" class="px-4 py-2 text-sm font-medium text-white bg-[#000000] rounded-md hover:bg-[#222222]">
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
          <IncidentForm
            :latitude="newIncidentLocation ? newIncidentLocation.lat : null"
            :longitude="newIncidentLocation ? newIncidentLocation.lng : null"
            @close="showIncidentForm = false"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/auth.store';
import { useIncidentStore } from '../stores/incident.store';
import { useRouter } from 'vue-router';
import { onMounted, ref, watchEffect } from 'vue';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import IncidentForm from '../components/IncidentForm.vue';

const authStore = useAuthStore();
const incidentStore = useIncidentStore();
const router = useRouter();
const showIncidentForm = ref(false);
const newIncidentLocation = ref<{ lat: number; lng: number } | null>(null);
const map = ref<L.Map | null>(null);
const markerLayer = ref<L.LayerGroup | null>(null);

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

// helper to open the report modal from header
function openReport() {
  // dispatch a global event so map or other components can handle it if needed
  window.dispatchEvent(new CustomEvent('open-report-modal'))
}

onMounted(() => {
  // Initialize map
  // Coordinates
  const defaultCenter: [number, number] = [9.145, 38.7667] // central Addis Ababa
  // Piassa / Addis Ababa Police HQ (approximate) - if you have a more accurate lat/lng, we can replace this
  const piassaCenter: [number, number] = [9.0215, 38.7612]

  const initialCenter = authStore.user?.role === 'SUPER_USER' ? piassaCenter : defaultCenter

  map.value = L.map('map-container').setView(initialCenter, 13) // Initialize map
  // Base layers
  const streetLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  });

  // Add street as the default base layer
  streetLayer.addTo(map.value);

  markerLayer.value = L.layerGroup().addTo(map.value);

  // If the logged-in user is SUPER_USER, add an HQ marker at Piassa and ensure the map centers there
  if (authStore.user?.role === 'SUPER_USER') {
    const hqIcon = L.divIcon({
      className: 'hq-div-icon',
      html: `<div class="hq-pin"></div>`,
      iconSize: [28, 28],
      iconAnchor: [14, 28]
    })

    const hqMarker = L.marker(piassaCenter, { icon: hqIcon }).addTo(map.value)
    hqMarker.bindPopup('<b>Addis Ababa Police HQ (Piassa)</b>')
  }

  // Set up map click handler
  map.value.on('click', (e) => {
    // The map also dispatches a global event; keep the local behavior but centralize via window event
    const detail = { lat: e.latlng.lat, lng: e.latlng.lng }
    window.dispatchEvent(new CustomEvent('map-click', { detail }))
  });

  // Fetch incidents when the component mounts
  incidentStore.fetchIncidents();
});

// Listen for global events to open the incident form
window.addEventListener('map-click', (ev: any) => {
  newIncidentLocation.value = ev.detail ? { lat: ev.detail.lat, lng: ev.detail.lng } : null
  showIncidentForm.value = true
})

// listen for requests to open the report modal without a location
window.addEventListener('open-report-modal', () => {
  newIncidentLocation.value = null
  showIncidentForm.value = true
})

watchEffect(() => {
  if (markerLayer.value) {
    markerLayer.value.clearLayers();
    incidentStore.incidents.forEach(incident => {
      // Use a simple CSS-based pin for incidents (no inline SVG)
      const svgIcon = L.divIcon({
        className: 'custom-div-icon',
        html: `<div class="marker-pin"></div>`,
        iconSize: [24, 24],
        iconAnchor: [12, 24]
      });

      const marker = L.marker([incident.latitude, incident.longitude], { icon: svgIcon });
      marker.bindPopup(`<b>${incident.title}</b><br>${incident.description || ''}`);
      markerLayer.value?.addLayer(marker);
    });
  }
});
</script>

<style>
#map-container {
  height: 100%;
  width: 100%;
}

/* Simple CSS pins for markers */
.marker-pin {
  width: 16px;
  height: 16px;
  background: #6B7280; /* neutral gray */
  border: 2px solid #ffffff;
  border-radius: 50% 50% 50% 0;
  transform: rotate(-45deg) translateY(-2px);
  box-shadow: 0 1px 2px rgba(0,0,0,0.2);
}
.marker-pin::after {
  content: '';
  width: 8px;
  height: 8px;
  background: rgba(255,255,255,0.9);
  border-radius: 50%;
  position: relative;
  left: 4px;
  top: 4px;
  display: block;
  transform: rotate(45deg);
}
.hq-pin {
  width: 20px;
  height: 20px;
  background: #DC2626; /* red */
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.25);
}
</style>
</style>