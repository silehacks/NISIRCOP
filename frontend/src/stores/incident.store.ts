import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as IncidentService from '../services/incident.service';
import type { Incident } from '../models/incident.model';

export const useIncidentStore = defineStore('incident', () => {
  const incidents = ref<Incident[]>([]);
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

  async function addIncident(incidentData: Omit<Incident, 'id' | 'reportedBy' | 'occurredAt'>) {
    isLoading.value = true;
    try {
      const response = await IncidentService.createIncident(incidentData);
      incidents.value.push(response.data);
    } catch (error) {
      console.error('Failed to create incident:', error);
      throw error; // Re-throw to be handled by the component
    } finally {
      isLoading.value = false;
    }
  }

  async function updateIncident(incidentData: Incident) {
    if (!incidentData.id) {
      console.error("Update operation called without an incident ID.");
      return;
    }
    isLoading.value = true;
    try {
      const response = await IncidentService.updateIncident(incidentData.id, incidentData);
      const index = incidents.value.findIndex(i => i.id === incidentData.id);
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

  async function deleteIncident(id: number) {
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