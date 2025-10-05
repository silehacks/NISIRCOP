<template>
  <div class="p-6">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-xl font-semibold text-black">Incidents</h2>
      <div>
        <button @click="refresh" class="px-3 py-1 bg-[#1478f0] text-white rounded-md">Refresh</button>
      </div>
    </div>

    <div v-if="store.isLoading" class="text-sm text-gray-600">Loading incidents...</div>
    <div v-else>
      <table class="min-w-full bg-[#ffffff] shadow rounded"> 
        <thead>
          <tr class="text-left">
            <th class="px-4 py-2">ID</th>
            <th class="px-4 py-2">Title</th>
            <th class="px-4 py-2">Type</th>
            <th class="px-4 py-2">Priority</th>
            <th class="px-4 py-2">Location</th>
            <th class="px-4 py-2">Reported By</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="inc in store.incidents" :key="inc.id" class="border-t hover:bg-[#dcf0f0] cursor-pointer" @click="() => focusIncident(inc)">
            <td class="px-4 py-2">{{ inc.id }}</td>
            <td class="px-4 py-2">{{ inc.title }}</td>
            <td class="px-4 py-2">{{ inc.incidentType || '-' }}</td>
            <td class="px-4 py-2">{{ inc.priority || '-' }}</td>
            <td class="px-4 py-2">{{ inc.latitude }}, {{ inc.longitude }}</td>
            <td class="px-4 py-2">{{ inc.reporterId || '-' }}</td>
          </tr>
        </tbody>
      </table>

      <div v-if="store.incidents.length === 0" class="mt-4 text-sm text-gray-600">No incidents found.</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useIncidentStore } from '../stores/incident.store'

const store = useIncidentStore()

const refresh = async () => {
  await store.fetchIncidents()
}

onMounted(() => {
  if (store.incidents.length === 0) store.fetchIncidents()
})

const focusIncident = (inc: any) => {
  if (!inc) return
  const lat = inc.latitude
  const lng = inc.longitude
  if (typeof lat === 'number' && typeof lng === 'number') {
    window.dispatchEvent(new CustomEvent('focus-incident', { detail: { lat, lng, id: inc.id } }))
  }
}
</script>

<style scoped>
</style>
