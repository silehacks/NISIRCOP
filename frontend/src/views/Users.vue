<template>
  <div class="text-[#C1E8FF]">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-3xl font-extrabold">User Management</h2>
      <button @click="openCreateModal" class="px-4 py-2 text-sm font-medium text-white bg-[#5483B3] rounded-md hover:bg-[#7DA0CA] transition-colors">
        Create New User
      </button>
    </div>

    <!-- Search and Filter Controls -->
    <div class="mb-6 bg-[#052659] p-4 rounded-lg">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label for="search" class="block text-sm font-medium text-[#7DA0CA] mb-2">Search</label>
          <input
            v-model="searchQuery"
            type="text"
            id="search"
            placeholder="Search users..."
            class="w-full px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          />
        </div>
        <div>
          <label for="roleFilter" class="block text-sm font-medium text-[#7DA0CA] mb-2">Role</label>
          <select
            v-model="roleFilter"
            id="roleFilter"
            class="w-full px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          >
            <option value="">All Roles</option>
            <option value="SUPER_USER">Super User</option>
            <option value="POLICE_STATION">Police Station</option>
            <option value="OFFICER">Officer</option>
          </select>
        </div>
        <div>
          <label for="statusFilter" class="block text-sm font-medium text-[#7DA0CA] mb-2">Status</label>
          <select
            v-model="statusFilter"
            id="statusFilter"
            class="w-full px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          >
            <option value="">All Statuses</option>
            <option value="true">Active</option>
            <option value="false">Inactive</option>
          </select>
        </div>
      </div>
    </div>

    <div v-if="userStore.isLoading" class="text-center p-8">
      <p>Loading users...</p>
    </div>
    <div v-else class="bg-[#052659] rounded-lg shadow-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full">
        <thead class="bg-[#021024]">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">User</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Role</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">Status</th>
            <th scope="col" class="relative px-6 py-3"><span class="sr-only">Actions</span></th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#5483B3]">
          <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-[#5483B3] hover:bg-opacity-20">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium">{{ user.username }}</div>
              <div class="text-xs text-[#7DA0CA]">{{ user.email }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">{{ user.role }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="user.isActive ? 'bg-green-900 text-green-300' : 'bg-red-900 text-red-300'" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                {{ user.isActive ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button @click="openEditModal(user)" class="text-[#7DA0CA] hover:text-white mr-4">Edit</button>
              <button @click="handleDeleteUser(user.id)" class="text-red-400 hover:text-red-300">Delete</button>
            </td>
          </tr>
        </tbody>
        </table>
      </div>
      <div v-if="filteredUsers.length === 0" class="text-center p-8">
        <p>{{ userStore.users.length === 0 ? 'No users found.' : 'No users match your filters.' }}</p>
      </div>
    </div>

    <UserFormModal
      :is-open="isModalOpen"
      :user-to-edit="selectedUser"
      @close="closeModal"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '../stores/user.store';
import UserFormModal from '../components/UserFormModal.vue';

const userStore = useUserStore();
const isModalOpen = ref(false);
const selectedUser = ref<any | null>(null);
const searchQuery = ref('');
const roleFilter = ref('');
const statusFilter = ref('');

const filteredUsers = computed(() => {
  let filtered = userStore.users;

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(user =>
      user.username.toLowerCase().includes(query) ||
      user.email?.toLowerCase().includes(query) ||
      user.firstName?.toLowerCase().includes(query) ||
      user.lastName?.toLowerCase().includes(query)
    );
  }

  // Role filter
  if (roleFilter.value) {
    filtered = filtered.filter(user => user.role === roleFilter.value);
  }

  // Status filter
  if (statusFilter.value) {
    const isActive = statusFilter.value === 'true';
    filtered = filtered.filter(user => user.isActive === isActive);
  }

  return filtered;
});

onMounted(() => {
  userStore.fetchUsers();
});

const openCreateModal = () => {
  selectedUser.value = null;
  isModalOpen.value = true;
};

const openEditModal = (user: any) => {
  selectedUser.value = user;
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  selectedUser.value = null;
};

const handleFormSubmit = async (formData: any) => {
  try {
    if (selectedUser.value) {
      // Update user
      await userStore.updateUser(selectedUser.value.id, formData);
    } else {
      // Create user
      await userStore.addUser(formData);
    }
    closeModal();
  } catch (error) {
    console.error('Failed to save user:', error);
    // The modal will show its own error
  }
};

const handleDeleteUser = async (id: number) => {
  if (confirm('Are you sure you want to delete this user?')) {
    try {
      await userStore.deleteUser(id);
    } catch (error) {
      console.error('Failed to delete user:', error);
      alert('Failed to delete user.');
    }
  }
};
</script>