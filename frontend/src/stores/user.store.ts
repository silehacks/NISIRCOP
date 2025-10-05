import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as UserService from '../services/users.service';

export const useUserStore = defineStore('user', () => {
  const users = ref([]);
  const isLoading = ref(false);

  async function fetchUsers() {
    isLoading.value = true;
    try {
      const response = await UserService.getUsers();
      users.value = response.data;
    } catch (error) {
      console.error('Failed to fetch users:', error);
      users.value = [];
    } finally {
      isLoading.value = false;
    }
  }

  async function addUser(userData) {
    isLoading.value = true;
    try {
      const response = await UserService.createUser(userData);
      users.value.push(response.data);
    } catch (error) {
      console.error('Failed to create user:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  async function updateUser(id, userData) {
    isLoading.value = true;
    try {
      const response = await UserService.updateUser(id, userData);
      const index = users.value.findIndex(u => u.id === id);
      if (index !== -1) {
        users.value[index] = response.data;
      }
    } catch (error) {
      console.error('Failed to update user:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  async function deleteUser(id) {
    isLoading.value = true;
    try {
      await UserService.deleteUser(id);
      users.value = users.value.filter(u => u.id !== id);
    } catch (error) {
      console.error('Failed to delete user:', error);
      throw error;
    } finally {
      isLoading.value = false;
    }
  }

  return {
    users,
    isLoading,
    fetchUsers,
    addUser,
    updateUser,
    deleteUser,
  };
});