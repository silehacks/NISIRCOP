<template>
  <div class="relative h-full">
    <div id="map-container" class="h-full w-full"></div>
    <div class="absolute top-4 right-4 z-[1000] bg-[#052659] p-2 rounded-lg shadow-lg">
      <label class="flex items-center cursor-pointer">
        <span class="mr-2 text-sm font-medium text-white">Show Heatmap</span>
        <div class="relative">
          <input type="checkbox" v-model="showHeatmap" class="sr-only">
          <div class="block bg-gray-600 w-10 h-6 rounded-full"></div>
          <div :class="{'translate-x-4': showHeatmap}" class="dot absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform"></div>
        </div>
      </label>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect, watch } from 'vue';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import 'leaflet.heat';

import { useIncidentStore } from '../stores/incident.store';
import { useAuthStore } from '../stores/auth.store';
import { useAnalyticsStore } from '../stores/analytics.store';
import { useUiStore } from '../stores/ui.store';
import { useGeoStore } from '../stores/geographic.store';

import '../assets/map-styles.css';

const incidentStore = useIncidentStore();
const authStore = useAuthStore();
const analyticsStore = useAnalyticsStore();
const uiStore = useUiStore();
const geoStore = useGeoStore();

const map = ref<L.Map | null>(null);
const markerLayer = ref<L.LayerGroup | null>(null);
const boundaryLayer = ref<L.GeoJSON | null>(null);
const heatLayer = ref<L.HeatLayer | null>(null);
const showHeatmap = ref(false);

const HQ_COORDS: [number, number] = [9.0215, 38.7612];
const ADDIS_ABABA_COORDS: [number, number] = [9.02497, 38.74689];

onMounted(async () => {
  if (authStore.user) {
    await geoStore.fetchBoundary(authStore.user.id);
  }

  let initialCenter = ADDIS_ABABA_COORDS;
  if (geoStore.boundary) {
    // A simple way to find the center of the boundary
    const bounds = L.geoJSON(geoStore.boundary).getBounds();
    if (bounds.isValid()) {
      initialCenter = bounds.getCenter();
    }
  } else if (authStore.user?.role === 'SUPER_USER') {
    initialCenter = HQ_COORDS;
  }

  map.value = L.map('map-container').setView(initialCenter, 13);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map.value);

  markerLayer.value = L.layerGroup().addTo(map.value);

  if (authStore.user?.role === 'SUPER_USER') {
    const hqIcon = L.divIcon({
      className: 'hq-div-icon',
      html: `<div class="hq-pin"></div>`,
      iconSize: [28, 28],
      iconAnchor: [14, 28]
    });
    L.marker(HQ_COORDS, { icon: hqIcon }).addTo(map.value).bindPopup('<b>Addis Ababa Police HQ (Piassa)</b>');
  }

  map.value.on('click', (e) => {
    uiStore.openReportIncidentModal({ lat: e.latlng.lat, lng: e.latlng.lng });
  });

  incidentStore.fetchIncidents();
  analyticsStore.fetchAnalytics();
});

// Watch for changes in incidents and update markers
watchEffect(() => {
  if (!markerLayer.value || !map.value) return;
  markerLayer.value.clearLayers();
  incidentStore.incidents.forEach(incident => {
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
});

// Watch for changes in the heatmap toggle
watch(showHeatmap, (isShown) => {
  if (!map.value) return;
  if (isShown) {
    const points = analyticsStore.incidentLocations
      .map(loc => [loc.latitude, loc.longitude] as L.LatLngTuple);
    heatLayer.value = L.heatLayer(points, { radius: 25 }).addTo(map.value);
  } else if (heatLayer.value) {
    map.value.removeLayer(heatLayer.value);
    heatLayer.value = null;
  }
});

// Watch for requests from other components to focus on a specific location
watch(() => uiStore.mapFocus, (focus) => {
  if (focus && map.value) {
    map.value.setView([focus.lat, focus.lng], focus.zoom || 15, { animate: true });
    // Attempt to open the popup for the nearest marker
    markerLayer.value?.eachLayer((layer: any) => {
      const latlng = layer.getLatLng?.();
      if (latlng && Math.abs(latlng.lat - focus.lat) < 0.0005 && Math.abs(latlng.lng - focus.lng) < 0.0005) {
        layer.openPopup?.();
      }
    });
    uiStore.clearMapFocus(); // Reset the focus request
  }
});

// Watch for changes in the user's boundary and draw it on the map
watchEffect(() => {
  if (boundaryLayer.value) {
    map.value?.removeLayer(boundaryLayer.value);
  }
  if (geoStore.boundary && map.value) {
    boundaryLayer.value = L.geoJSON(geoStore.boundary, {
      style: {
        color: "#5483B3",
        weight: 2,
        opacity: 0.65
      }
    }).addTo(map.value);
  }
});
</script>

<style>
#map-container { height: 100%; width: 100%; z-index: 1; }
</style>