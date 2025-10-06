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
          <p v-if="validationErrors.title" class="mt-1 text-sm text-red-400">{{ validationErrors.title }}</p>
        </div>

        <div>
          <label for="description" class="block text-sm font-medium text-[#7DA0CA]">Description</label>
          <textarea v-model="form.description" id="description" rows="3" class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"></textarea>
          <p v-if="validationErrors.description" class="mt-1 text-sm text-red-400">{{ validationErrors.description }}</p>
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

        <div v-if="validationErrors.location" class="text-red-400 text-sm text-center">
          {{ validationErrors.location }}
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
import { useAuthStore } from '../stores/auth.store';
import * as GeoService from '../services/geographic.service';

const props = defineProps<{
  isOpen: boolean;
  latitude: number | null;
  longitude: number | null;
}>();

const emit = defineEmits(['close']);

const incidentStore = useIncidentStore();
const authStore = useAuthStore();
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
const validationErrors = ref<Record<string, string>>({});

watch(() => props.latitude, (newVal) => form.value.latitude = newVal);
watch(() => props.longitude, (newVal) => form.value.longitude = newVal);

const validateForm = async () => {
  validationErrors.value = {};
  
  if (!form.value.title.trim()) {
    validationErrors.value.title = 'Title is required';
  }
  
  if (!form.value.latitude || !form.value.longitude) {
    validationErrors.value.location = 'Location coordinates are required';
  }
  
  if (form.value.title.length > 100) {
    validationErrors.value.title = 'Title must be less than 100 characters';
  }
  
  if (form.value.description && form.value.description.length > 500) {
    validationErrors.value.description = 'Description must be less than 500 characters';
  }
  
  // Validate location is within user's jurisdiction (skip for SUPER_USER)
  if (authStore.user?.role !== 'SUPER_USER' && form.value.latitude && form.value.longitude && authStore.user?.id) {
    try {
      const response = await GeoService.validateLocation(form.value.latitude, form.value.longitude, authStore.user.id);
      if (!response.data.valid) {
        validationErrors.value.location = 'This location is outside your jurisdiction';
      }
    } catch (err) {
      console.warn('Could not validate location:', err);
      // Don't block submission if validation service is unavailable
    }
  }
  
  return Object.keys(validationErrors.value).length === 0;
};

const submitIncident = async () => {
  error.value = null;
  validationErrors.value = {};
  
  if (!(await validateForm())) {
    return;
  }
  
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