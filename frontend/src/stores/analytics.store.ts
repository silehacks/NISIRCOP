import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as AnalyticsService from '../services/analytics.service';

interface IncidentLocation {
  latitude: number;
  longitude: number;
}

export const useAnalyticsStore = defineStore('analytics', () => {
  const incidentsByType = ref<{ name: string; count: number }[]>([]);
  const incidentsByPriority = ref<{ name: string; count: number }[]>([]);
  const incidentLocations = ref<IncidentLocation[]>([]);
  const isLoading = ref(false);

  async function fetchAnalytics() {
    isLoading.value = true;
    try {
      const [typeResponse, priorityResponse, locationsResponse] = await Promise.all([
        AnalyticsService.getCountByType(),
        AnalyticsService.getCountByPriority(),
        AnalyticsService.getIncidentLocations(),
      ]);
      incidentsByType.value = typeResponse.data;
      incidentsByPriority.value = priorityResponse.data;
      incidentLocations.value = locationsResponse.data;
    } catch (error) {
      console.error('Failed to fetch analytics data:', error);
      incidentsByType.value = [];
      incidentsByPriority.value = [];
      incidentLocations.value = [];
    } finally {
      isLoading.value = false;
    }
  }

  return {
    incidentsByType,
    incidentsByPriority,
    incidentLocations,
    isLoading,
    fetchAnalytics,
  };
});