import apiClient from './apiClient';

export const getIncidents = () => {
  return apiClient.get('/incidents');
};

export const createIncident = (incidentData) => {
  return apiClient.post('/incidents', incidentData);
};

export const getIncidentById = (id) => {
  return apiClient.get(`/incidents/${id}`);
};