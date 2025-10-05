import apiClient from './apiClient';

export const getUsers = () => {
  return apiClient.get('/users');
}

export const createUser = (userData: Record<string, any>) => {
  return apiClient.post('/users', userData);
}

export const getUserById = (id: number | string) => {
  return apiClient.get(`/users/${id}`);
}
