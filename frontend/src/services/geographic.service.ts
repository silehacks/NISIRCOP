import apiClient from './apiClient';

export const getBoundaryByUserId = (userId: number) => {
  return apiClient.get(`/geo/boundary/${userId}`);
};

export const validateLocation = (latitude: number, longitude: number, userId: number) => {
  return apiClient.post('/geo/validate-location', {
    latitude,
    longitude,
    userId
  });
};