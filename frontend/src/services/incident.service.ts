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

export const updateIncident = (id, incidentData) => {
  return apiClient.put(`/incidents/${id}`, incidentData);
};

export const deleteIncident = (id) => {
  return apiClient.delete(`/incidents/${id}`);
};