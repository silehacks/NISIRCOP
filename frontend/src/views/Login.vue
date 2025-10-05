<template>
  <div class="flex items-center justify-center min-h-screen bg-[#021024]">
    <div class="w-full max-w-md p-8 space-y-8 bg-[#052659] rounded-2xl shadow-2xl">
      <div class="text-center">
        <h2 class="text-4xl font-extrabold text-[#C1E8FF]">
          Welcome Back
        </h2>
        <p class="mt-2 text-md text-[#7DA0CA]">
          Sign in to access the NISIRCOP-LE Platform
        </p>
      </div>
      <form @submit.prevent="handleLogin" class="space-y-6">
        <div class="relative">
          <input
            id="username"
            v-model="username"
            type="text"
            required
            placeholder="Username"
            class="peer w-full px-4 py-3 text-[#C1E8FF] bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg placeholder-transparent focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          />
          <label
            for="username"
            class="absolute left-4 -top-3.5 text-[#7DA0CA] text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3.5 peer-focus:-top-3.5 peer-focus:text-[#7DA0CA] peer-focus:text-sm"
          >
            Username
          </label>
        </div>
        <div class="relative">
          <input
            id="password"
            v-model="password"
            type="password"
            required
            placeholder="Password"
            class="peer w-full px-4 py-3 text-[#C1E8FF] bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg placeholder-transparent focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
          />
          <label
            for="password"
            class="absolute left-4 -top-3.5 text-[#7DA0CA] text-sm transition-all peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-400 peer-placeholder-shown:top-3.5 peer-focus:-top-3.5 peer-focus:text-[#7DA0CA] peer-focus:text-sm"
          >
            Password
          </label>
        </div>
        <div v-if="error" class="text-red-400 text-sm text-center">
          {{ error }}
        </div>
        <div>
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full px-4 py-3 text-lg font-semibold text-white bg-[#5483B3] border border-transparent rounded-lg shadow-sm hover:bg-[#7DA0CA] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#7DA0CA] transition-colors duration-300 disabled:opacity-50"
          >
            <span v-if="isLoading">Signing in...</span>
            <span v-else>Sign In</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth.store';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const error = ref<string | null>(null);
const isLoading = ref(false);

const authStore = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
  error.value = null;
  isLoading.value = true;
  try {
    await authStore.login(username.value, password.value);
    router.push('/');
  } catch (err: any) {
    console.error('Login failed:', err);
    error.value = 'Login failed. Please check your credentials and try again.';
  } finally {
    isLoading.value = false;
  }
};
</script>