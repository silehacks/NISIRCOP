import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as AnalyticsService from '../services/analytics.service';

export const useAnalyticsStore = defineStore('analytics', () => {
  const incidentsByType = ref<{ name: string; count: number }[]>([]);
  const incidentsByPriority = ref<{ name: string; count: number }[]>([]);
  const isLoading = ref(false);

  async function fetchAnalytics() {
    isLoading.value = true;
    try {
      const [typeResponse, priorityResponse] = await Promise.all([
        AnalyticsService.getCountByType(),
        AnalyticsService.getCountByPriority(),
      ]);
      incidentsByType.value = typeResponse.data;
      incidentsByPriority.value = priorityResponse.data;
    } catch (error) {
      console.error('Failed to fetch analytics data:', error);
      incidentsByType.value = [];
      incidentsByPriority.value = [];
    } finally {
      isLoading.value = false;
    }
  }

  return {
    incidentsByType,
    incidentsByPriority,
    isLoading,
    fetchAnalytics,
  };
});