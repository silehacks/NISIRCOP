<template>
  <div class="p-6">
    <h2 class="text-xl font-semibold mb-4 text-black">Analytics</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div class="bg-white p-4 rounded shadow">
        <h3 class="font-semibold mb-2">Incidents by Type</h3>
        <ul>
          <li v-for="row in byType" :key="row.type">{{ row.type }}: {{ row.count }}</li>
        </ul>
      </div>
      <div class="bg-white p-4 rounded shadow">
        <h3 class="font-semibold mb-2">Incidents by Priority</h3>
        <ul>
          <li v-for="row in byPriority" :key="row.priority">{{ row.priority }}: {{ row.count }}</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as AnalyticsService from '../services/analytics.service'

const byType = ref<any[]>([])
const byPriority = ref<any[]>([])

const load = async () => {
  try {
    const t = await AnalyticsService.getCountByType()
    byType.value = t.data
  } catch (e) { byType.value = [] }
  try {
    const p = await AnalyticsService.getCountByPriority()
    byPriority.value = p.data
  } catch (e) { byPriority.value = [] }
}

onMounted(load)
</script>

<style scoped>
</style>
