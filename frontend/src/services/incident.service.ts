import apiClient from './apiClient';
import type { Incident } from '../models/incident.model';

export const getIncidents = () => {
  return apiClient.get<Incident[]>('/incidents');
};

export const createIncident = (incidentData: Omit<Incident, 'id' | 'reportedBy' | 'occurredAt'>) => {
  return apiClient.post<Incident>('/incidents', incidentData);
};

export const getIncidentById = (id: number) => {
  return apiClient.get<Incident>(`/incidents/${id}`);
};

export const updateIncident = (id: number, incidentData: Partial<Incident>) => {
  return apiClient.put<Incident>(`/incidents/${id}`, incidentData);
};

export const deleteIncident = (id: number) => {
  return apiClient.delete(`/incidents/${id}`);
};