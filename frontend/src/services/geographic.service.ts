import apiClient from './apiClient';

export const getBoundaryByUserId = (userId: number) => {
  return apiClient.get(`/geo/boundary/${userId}`);
};