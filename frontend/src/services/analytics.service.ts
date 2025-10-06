import apiClient from './apiClient'

export const getCountByType = () => apiClient.get('/analytics/incidents/count-by-type')
export const getCountByPriority = () => apiClient.get('/analytics/incidents/count-by-priority')
export const getIncidentLocations = () => apiClient.get('/analytics/incidents/locations')
