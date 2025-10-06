import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as GeoService from '../services/geographic.service';

export const useGeoStore = defineStore('geographic', () => {
  const boundary = ref(null);
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  async function fetchBoundary(userId: number) {
    isLoading.value = true;
    error.value = null;
    try {
      const response = await GeoService.getBoundaryByUserId(userId);
      boundary.value = response.data;
    } catch (err) {
      console.error('Failed to fetch boundary:', err);
      error.value = 'Failed to load user boundary.';
      boundary.value = null;
    } finally {
      isLoading.value = false;
    }
  }

  return {
    boundary,
    isLoading,
    error,
    fetchBoundary,
  };
});