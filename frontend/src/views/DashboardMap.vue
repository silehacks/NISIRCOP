<template>
  <div class="h-full" id="map-container"></div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { useIncidentStore } from '../stores/incident.store'
import { useAuthStore } from '../stores/auth.store'

const incidentStore = useIncidentStore()
const authStore = useAuthStore()

const map = ref<L.Map | null>(null)
const markerLayer = ref<L.LayerGroup | null>(null)

onMounted(() => {
  const defaultCenter: [number, number] = [9.145, 38.7667]
  const piassaCenter: [number, number] = [9.0215, 38.7612]
  const initialCenter = authStore.user?.role === 'SUPER_USER' ? piassaCenter : defaultCenter

  map.value = L.map('map-container').setView(initialCenter, 13)

  const streetLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  })
  streetLayer.addTo(map.value)

  markerLayer.value = L.layerGroup().addTo(map.value)

  if (authStore.user?.role === 'SUPER_USER') {
    // use a CSS-based pin for HQ instead of inline SVG
    const hqIcon = L.divIcon({
      className: 'hq-div-icon',
      html: `<div class="hq-pin" />`,
      iconSize: [28, 28],
      iconAnchor: [14, 28]
    })
    L.marker(piassaCenter, { icon: hqIcon }).addTo(map.value).bindPopup('<b>Addis Ababa Police HQ (Piassa)</b>')
  }

  map.value.on('click', (e) => {
    // Emit a custom event so parent layout can open the report form
    const detail = { lat: e.latlng.lat, lng: e.latlng.lng }
    window.dispatchEvent(new CustomEvent('map-click', { detail }))
  })

  incidentStore.fetchIncidents()
})

watchEffect(() => {
  if (markerLayer.value) {
    markerLayer.value.clearLayers()
    incidentStore.incidents.forEach(incident => {
      // use a simple CSS-based pin for incident markers (no inline SVG)
      const svgIcon = L.divIcon({
        className: 'custom-div-icon',
        html: `<div class="marker-pin"></div>`,
        iconSize: [24, 24],
        iconAnchor: [12, 24]
      })
      const marker = L.marker([incident.latitude, incident.longitude], { icon: svgIcon })
      marker.bindPopup(`<b>${incident.title}</b><br>${incident.description || ''}`)
      markerLayer.value?.addLayer(marker)
    })
  }
})

// allow external components to request focusing an incident on the map
window.addEventListener('focus-incident', (ev: any) => {
  const detail = ev.detail
  if (!map.value) return
  const lat = detail?.lat
  const lng = detail?.lng
  if (typeof lat === 'number' && typeof lng === 'number') {
    map.value.setView([lat, lng], 15, { animate: true })
    // try to open the popup for the nearest marker
    markerLayer.value?.eachLayer((layer: any) => {
      try {
        const latlng = layer.getLatLng && layer.getLatLng()
        if (latlng && Math.abs(latlng.lat - lat) < 0.0005 && Math.abs(latlng.lng - lng) < 0.0005) {
          layer.openPopup && layer.openPopup()
        }
      } catch (e) { /* ignore */ }
    })
  }
})
</script>

<style>
#map-container { height: 100%; width: 100%; }
</style>
