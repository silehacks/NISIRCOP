<template>
  <div class="text-[#C1E8FF]">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-3xl font-extrabold">Incidents</h2>
      <button @click="refresh" class="px-4 py-2 text-sm font-medium text-white bg-[#5483B3] rounded-md hover:bg-[#7DA0CA] transition-colors">
        Refresh
      </button>
    </div>

    <div v-if="incidentStore.isLoading" class="text-center p-8">
      <p>Loading incidents...</p>
    </div>
    <div v-else class="bg-[#052659] rounded-lg shadow-lg overflow-hidden">
      <table class="min-w-full">
        <thead class="bg-[#021024]">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Title</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Type</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Priority</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Status</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Reported At</th>
            <th scope="col" class="relative px-6 py-3">
              <span class="sr-only">Actions</span>
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#5483B3]">
          <tr v-for="incident in incidentStore.incidents" :key="incident.id" class="hover:bg-[#5483B3] hover:bg-opacity-20">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium">{{ incident.title }}</div>
              <div class="text-xs text-[#7DA0CA]">{{ incident.id }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ incident.incidentType || '-' }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="getPriorityClass(incident.priority)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                {{ incident.priority || '-' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ incident.status || 'Open' }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ new Date(incident.occurredAt).toLocaleString() }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button @click="focusIncident(incident)" class="text-[#7DA0CA] hover:text-white mr-4">View on Map</button>
              <button v-if="canEditDelete(incident)" @click="editIncident(incident)" class="text-[#7DA0CA] hover:text-white mr-4">Edit</button>
              <button v-if="canEditDelete(incident)" @click="deleteIncident(incident.id)" class="text-red-400 hover:text-red-300">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="incidentStore.incidents.length === 0" class="text-center p-8">
        <p>No incidents found.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useIncidentStore } from '../stores/incident.store';
import { useAuthStore } from '../stores/auth.store';

const incidentStore = useIncidentStore();
const authStore = useAuthStore();

const refresh = async () => {
  await incidentStore.fetchIncidents();
};

onMounted(() => {
  if (incidentStore.incidents.length === 0) {
    incidentStore.fetchIncidents();
  }
});

const focusIncident = (incident: any) => {
  if (!incident) return;
  const lat = incident.latitude;
  const lng = incident.longitude;
  if (typeof lat === 'number' && typeof lng === 'number') {
    window.dispatchEvent(new CustomEvent('focus-incident', { detail: { lat, lng, id: incident.id } }));
  }
};

const editIncident = (incident: any) => {
  // This would typically open a modal or navigate to an edit page.
  // For now, we'll just log it.
  console.log('Editing incident:', incident);
  alert('Edit functionality is not yet fully implemented.');
};

const deleteIncident = async (id: number) => {
  if (confirm('Are you sure you want to delete this incident?')) {
    try {
      await incidentStore.deleteIncident(id);
    } catch (error) {
      console.error('Failed to delete incident:', error);
      alert('Failed to delete incident.');
    }
  }
};

const canEditDelete = (incident: any) => {
  if (!authStore.user) return false;
  const userRole = authStore.user.role;
  const userId = authStore.user.id;
  return userRole === 'SUPER_USER' || incident.reportedBy === userId;
};

const getPriorityClass = (priority: string) => {
  switch (priority) {
    case 'High': return 'bg-red-900 text-red-300';
    case 'Medium': return 'bg-yellow-900 text-yellow-300';
    case 'Low': return 'bg-green-900 text-green-300';
    default: return 'bg-gray-700 text-gray-300';
  }
};
</script>