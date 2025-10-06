<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-70">
    <div class="w-full max-w-lg p-8 space-y-6 bg-[#052659] rounded-2xl shadow-2xl text-[#C1E8FF]">
      <div class="flex justify-between items-center">
        <h2 class="text-3xl font-extrabold">Report New Incident</h2>
        <button @click="$emit('close')" class="p-2 rounded-full hover:bg-[#5483B3]">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
        </button>
      </div>

      <form @submit.prevent="submitIncident" class="space-y-4">
        <div>
          <label for="title" class="block text-sm font-medium text-[#7DA0CA]">Title</label>
          <input v-model="form.title" type="text" id="title" required class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]">
        </div>

        <div>
          <label for="description" class="block text-sm font-medium text-[#7DA0CA]">Description</label>
          <textarea v-model="form.description" id="description" rows="3" class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"></textarea>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="incidentType" class="block text-sm font-medium text-[#7DA0CA]">Incident Type</label>
            <input v-model="form.incidentType" type="text" id="incidentType" class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]">
          </div>
          <div>
            <label for="priority" class="block text-sm font-medium text-[#7DA0CA]">Priority</label>
            <select v-model="form.priority" id="priority" required class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]">
              <option>Low</option>
              <option>Medium</option>
              <option>High</option>
            </select>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-[#7DA0CA]">Latitude</label>
            <input :value="form.latitude" type="text" disabled class="w-full mt-1 px-4 py-2 bg-[#021024] border border-[#5483B3] rounded-lg">
          </div>
          <div>
            <label class="block text-sm font-medium text-[#7DA0CA]">Longitude</label>
            <input :value="form.longitude" type="text" disabled class="w-full mt-1 px-4 py-2 bg-[#021024] border border-[#5483B3] rounded-lg">
          </div>
        </div>

        <div v-if="error" class="text-red-400 text-sm text-center">
          {{ error }}
        </div>

        <div class="pt-4">
          <button type="submit" :disabled="isLoading" class="w-full px-4 py-3 text-lg font-semibold text-white bg-[#5483B3] rounded-lg hover:bg-[#7DA0CA] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#7DA0CA] transition-colors disabled:opacity-50">
            <span v-if="isLoading">Submitting...</span>
            <span v-else>Submit Incident</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useIncidentStore } from '../stores/incident.store';

const props = defineProps<{
  isOpen: boolean;
  latitude: number | null;
  longitude: number | null;
}>();

const emit = defineEmits(['close']);

const incidentStore = useIncidentStore();
const form = ref({
  title: '',
  description: '',
  incidentType: '',
  priority: 'Medium',
  latitude: props.latitude,
  longitude: props.longitude,
});
const error = ref<string | null>(null);
const isLoading = ref(false);

watch(() => props.latitude, (newVal) => form.value.latitude = newVal);
watch(() => props.longitude, (newVal) => form.value.longitude = newVal);

const submitIncident = async () => {
  error.value = null;
  isLoading.value = true;
  try {
    await incidentStore.addIncident(form.value);
    emit('close');
    // Reset form
    form.value = {
      title: '',
      description: '',
      incidentType: '',
      priority: 'Medium',
      latitude: null,
      longitude: null,
    };
  } catch (err) {
    console.error('Failed to submit incident:', err);
    error.value = 'Failed to submit incident. Please try again.';
  } finally {
    isLoading.value = false;
  }
};
</script>