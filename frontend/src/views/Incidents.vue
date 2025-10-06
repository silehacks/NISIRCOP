<template>
  <div class="text-[#C1E8FF]">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-3xl font-extrabold">Incidents</h2>
      <button @click="refresh" class="px-4 py-2 text-sm font-medium text-white bg-[#5483B3] rounded-md hover:bg-[#7DA0CA] transition-colors">
        Refresh
      </button>
    </div>

    <!-- Search and Filter Controls -->
    <div class="mb-6 bg-[#052659] p-4 rounded-lg">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label for="search" class="block text-sm font-medium text-[#7DA0CA] mb-2">Search</label>
          <input
            v-model="searchQuery"
            type="text"
            id="search"
            placeholder="Search incidents..."
            class="w-full px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          />
        </div>
        <div>
          <label for="priorityFilter" class="block text-sm font-medium text-[#7DA0CA] mb-2">Priority</label>
          <select
            v-model="priorityFilter"
            id="priorityFilter"
            class="w-full px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          >
            <option value="">All Priorities</option>
            <option value="High">High</option>
            <option value="Medium">Medium</option>
            <option value="Low">Low</option>
          </select>
        </div>
        <div>
          <label for="statusFilter" class="block text-sm font-medium text-[#7DA0CA] mb-2">Status</label>
          <select
            v-model="statusFilter"
            id="statusFilter"
            class="w-full px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          >
            <option value="">All Statuses</option>
            <option value="Open">Open</option>
            <option value="InProgress">In Progress</option>
            <option value="Resolved">Resolved</option>
            <option value="Closed">Closed</option>
          </select>
        </div>
      </div>
    </div>

    <div v-if="incidentStore.isLoading" class="text-center p-8">
      <p>Loading incidents...</p>
    </div>
    <div v-else class="bg-[#052659] rounded-lg shadow-lg overflow-hidden">
      <div class="overflow-x-auto">
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
          <tr v-for="incident in filteredIncidents" :key="incident.id" class="hover:bg-[#5483B3] hover:bg-opacity-20">
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
      </div>
      <div v-if="filteredIncidents.length === 0" class="text-center p-8">
        <p>{{ incidentStore.incidents.length === 0 ? 'No incidents found.' : 'No incidents match your filters.' }}</p>
      </div>
    </div>
    <IncidentEditModal
      :show="showEditModal"
      :incident="selectedIncident"
      @close="showEditModal = false"
      @save="saveIncident"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useIncidentStore } from '../stores/incident.store';
import { useAuthStore } from '../stores/auth.store';
import { useUiStore } from '../stores/ui.store';
import IncidentEditModal from '../components/IncidentEditModal.vue';
import type { Incident } from '../models/incident.model';

const incidentStore = useIncidentStore();
const authStore = useAuthStore();
const uiStore = useUiStore();

const showEditModal = ref(false);
const selectedIncident = ref<Incident | null>(null);
const searchQuery = ref('');
const priorityFilter = ref('');
const statusFilter = ref('');

const filteredIncidents = computed(() => {
  let filtered = incidentStore.incidents;

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(incident =>
      incident.title.toLowerCase().includes(query) ||
      incident.description?.toLowerCase().includes(query) ||
      incident.incidentType?.toLowerCase().includes(query)
    );
  }

  // Priority filter
  if (priorityFilter.value) {
    filtered = filtered.filter(incident => incident.priority === priorityFilter.value);
  }

  // Status filter
  if (statusFilter.value) {
    filtered = filtered.filter(incident => incident.status === statusFilter.value);
  }

  return filtered;
});

const refresh = async () => {
  await incidentStore.fetchIncidents();
};

onMounted(() => {
  if (incidentStore.incidents.length === 0) {
    incidentStore.fetchIncidents();
  }
});

const focusIncident = (incident: Incident) => {
  if (!incident) return;
  const lat = incident.latitude;
  const lng = incident.longitude;
  if (typeof lat === 'number' && typeof lng === 'number') {
    uiStore.setMapFocus({ lat, lng, zoom: 15 });
  }
};

const editIncident = (incident: Incident) => {
  selectedIncident.value = incident;
  showEditModal.value = true;
};

const saveIncident = async (updatedIncident: Incident) => {
  if (!updatedIncident || !updatedIncident.id) return;
  try {
    await incidentStore.updateIncident(updatedIncident);
    showEditModal.value = false;
  } catch (error) {
    console.error('Failed to update incident:', error);
    alert('Failed to update incident.');
  }
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

const canEditDelete = (incident: Incident) => {
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