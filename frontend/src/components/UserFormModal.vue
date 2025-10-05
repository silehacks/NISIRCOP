<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-70">
    <div class="w-full max-w-2xl p-8 space-y-6 bg-[#052659] rounded-2xl shadow-2xl text-[#C1E8FF]">
      <div class="flex justify-between items-center">
        <h2 class="text-3xl font-extrabold">{{ isEditMode ? 'Edit User' : 'Create New User' }}</h2>
        <button @click="$emit('close')" class="p-2 rounded-full hover:bg-[#5483B3]">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
        </button>
      </div>

      <form @submit.prevent="submitForm" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input-field v-model="form.username" label="Username" id="username" required />
          <input-field v-model="form.email" label="Email" id="email" type="email" required />
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input-field v-model="form.firstName" label="First Name" id="firstName" required />
          <input-field v-model="form.lastName" label="Last Name" id="lastName" required />
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input-field v-model="form.phone" label="Phone Number" id="phone" />
          <input-field v-model="form.badgeNumber" label="Badge Number (if applicable)" id="badgeNumber" />
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="role" class="block text-sm font-medium text-[#7DA0CA]">Role</label>
            <select v-model="form.role" id="role" required class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]">
              <option v-for="role in availableRoles" :key="role" :value="role">{{ role }}</option>
            </select>
          </div>
          <input-field v-if="!isEditMode" v-model="form.password" label="Password" id="password" type="password" required />
        </div>
        <div>
          <label for="boundary" class="block text-sm font-medium text-[#7DA0CA]">Boundary (WKT Format, optional)</label>
          <textarea v-model="form.boundary" id="boundary" rows="3" class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"></textarea>
        </div>

        <div v-if="error" class="text-red-400 text-sm text-center">{{ error }}</div>

        <div class="pt-4">
          <button type="submit" :disabled="isLoading" class="w-full px-4 py-3 text-lg font-semibold text-white bg-[#5483B3] rounded-lg hover:bg-[#7DA0CA] disabled:opacity-50">
            <span v-if="isLoading">Saving...</span>
            <span v-else>{{ isEditMode ? 'Save Changes' : 'Create User' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useAuthStore } from '../stores/auth.store';
import InputField from './InputField.vue'; // A simple reusable input component

const props = defineProps<{
  isOpen: boolean;
  userToEdit: any | null;
}>();

const emit = defineEmits(['close', 'submit']);

const authStore = useAuthStore();
const form = ref<any>({});
const error = ref<string | null>(null);
const isLoading = ref(false);

const isEditMode = computed(() => !!props.userToEdit);

const availableRoles = computed(() => {
  const userRole = authStore.user?.role;
  if (userRole === 'SUPER_USER') return ['POLICE_STATION'];
  if (userRole === 'POLICE_STATION') return ['OFFICER'];
  return [];
});

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    if (isEditMode.value) {
      form.value = { ...props.userToEdit };
    } else {
      form.value = { role: availableRoles.value[0] || '' };
    }
  }
});

const submitForm = async () => {
  error.value = null;
  isLoading.value = true;
  try {
    emit('submit', form.value);
  } catch (err) {
    error.value = 'An error occurred.';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* You can create a reusable InputField.vue component or just use standard inputs */
</style>