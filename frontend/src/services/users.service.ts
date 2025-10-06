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

export const updateUser = (id: number, userData: Record<string, any>) => {
  return apiClient.put(`/users/${id}`, userData);
};

export const deleteUser = (id: number) => {
  return apiClient.delete(`/users/${id}`);
};
