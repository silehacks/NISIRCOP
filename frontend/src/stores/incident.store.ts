import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as IncidentService from '../services/incident.service';

export const useIncidentStore = defineStore('incident', () => {
  const incidents = ref([]);
  const isLoading = ref(false);

  async function fetchIncidents() {
    isLoading.value = true;
    try {
      const response = await IncidentService.getIncidents();
      incidents.value = response.data;
    } catch (error) {
      console.error('Failed to fetch incidents:', error);
      incidents.value = [];
    } finally {
      isLoading.value = false;
    }
  }

  async function addIncident(incidentData) {
    isLoading.value = true;
    try {
      const response = await IncidentService.createIncident(incidentData);
      // Add the new incident to the local state
      incidents.value.push(response.data);
    } catch (error) {
      console.error('Failed to create incident:', error);
      throw error; // Re-throw to be handled by the component
    } finally {
      isLoading.value = false;
    }
  }

  async function updateIncident(id, incidentData) {
    isLoading.value = true;
    try {
      const response = await IncidentService.updateIncident(id, incidentData);
      const index = incidents.value.findIndex(i => i.id === id);
      if (index !== -1) {
        incidents.value[index] = response.data;
      }
    } catch (error) {
      console.error('Failed to update incident:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  async function deleteIncident(id) {
    isLoading.value = true;
    try {
      await IncidentService.deleteIncident(id);
      incidents.value = incidents.value.filter(i => i.id !== id);
    } catch (error) {
      console.error('Failed to delete incident:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  return {
    incidents,
    isLoading,
    fetchIncidents,
    addIncident,
    updateIncident,
    deleteIncident,
  };
});