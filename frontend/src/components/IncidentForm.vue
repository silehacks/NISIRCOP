<template>
  <div class="p-8 bg-white rounded-lg shadow-md">
    <h2 class="text-2xl font-bold mb-6 text-black">Report New Incident</h2>
    <form @submit.prevent="handleSubmit">
      <div class="mb-4">
        <label for="title" class="block text-sm font-medium text-gray-700">Title</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
          required
          class="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        />
      </div>

      <div class="mb-4">
        <label for="description" class="block text-sm font-medium text-gray-700">Description</label>
        <textarea
          id="description"
          v-model="form.description"
          rows="4"
          class="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        ></textarea>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
        <div>
          <label for="incident_type" class="block text-sm font-medium text-gray-700">Incident Type</label>
          <select
            id="incident_type"
            v-model="form.incidentType"
            class="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option>Theft</option>
            <option>Assault</option>
            <option>Vandalism</option>
            <option>Traffic</option>
            <option>Other</option>
          </select>
        </div>
        <div>
          <label for="priority" class="block text-sm font-medium text-gray-700">Priority</label>
          <select
            id="priority"
            v-model="form.priority"
            required
            class="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option>Low</option>
            <option>Medium</option>
            <option>High</option>
            <option>Critical</option>
          </select>
        </div>
      </div>

      <!-- Location would be handled here, e.g., by clicking on the map -->
      <div class="mb-6">
        <p class="text-sm text-gray-600">Location: (will be set via map interaction)</p>
        <p v-if="form.latitude && form.longitude" class="text-sm font-medium text-gray-800">
          Lat: {{ form.latitude }}, Lon: {{ form.longitude }}
        </p>
      </div>

      <div class="flex justify-end space-x-4">
        <button
          type="button"
          @click="$emit('close')"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 border border-transparent rounded-md shadow-sm hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Close
        </button>
        <button
          type="submit"
          class="px-4 py-2 text-sm font-medium text-white bg-[#1478f0] border border-transparent rounded-md shadow-sm hover:bg-[#0f61c9] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#1478f0]"
        >
          Submit Report
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useIncidentStore } from '../stores/incident.store';

const props = defineProps<{
  latitude?: number | null;
  longitude?: number | null;
}>();

const emit = defineEmits(['close']);

const incidentStore = useIncidentStore();

const form = ref({
  title: '',
  description: '',
  incidentType: 'Other',
  priority: 'Medium',
  latitude: props.latitude ?? null,
  longitude: props.longitude ?? null,
});

// update form if props change
watchEffect(() => {
  form.value.latitude = props.latitude ?? form.value.latitude
  form.value.longitude = props.longitude ?? form.value.longitude
})

const handleSubmit = async () => {
  try {
    await incidentStore.addIncident(form.value);
    alert('Incident reported successfully!');
    // refresh global incident list
    await incidentStore.fetchIncidents();
    emit('close');
  } catch (error) {
    console.error('Failed to report incident:', error);
    alert('Failed to report incident. See console for details.');
  }
};
</script>