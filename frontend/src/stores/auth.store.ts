import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as AuthService from '../services/auth.service'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)

  function setAuthData(newToken: string, newUser: any) {
    user.value = newUser
    token.value = newToken
    localStorage.setItem('user', JSON.stringify(newUser))
    localStorage.setItem('token', newToken)
  }

  function clearAuthData() {
    user.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  async function login(username: string, password: string) {
    try {
      const response = await AuthService.login(username, password)
      const { jwt, ...userData } = response.data
      setAuthData(jwt, userData)
      return response.data
    } catch (error) {
      clearAuthData()
      throw error
    }
  }

  function logout() {
    clearAuthData()
  }

  // Check token validity on app startup
  function initialize() {
    if (token.value) {
      // Here you might want to add a call to a backend endpoint
      // to verify the token is still valid. For now, we'll assume it is.
      // User session restored from local storage
    } else {
      // No user session found
    }
  }

  initialize()

  return {
    token,
    user,
    isAuthenticated,
    login,
    logout,
  }
})