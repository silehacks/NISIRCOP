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
    } catch (error: any) {
      clearAuthData()
      // Provide more specific error messages
      if (error.response?.status === 401) {
        throw new Error('Invalid username or password')
      } else if (error.response?.status === 403) {
        throw new Error('Account is disabled')
      } else if (error.response?.status >= 500) {
        throw new Error('Server error. Please try again later.')
      } else {
        throw new Error('Login failed. Please check your connection and try again.')
      }
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
      console.log('User is authenticated from local storage.')
    } else {
      console.log('No user session found in local storage.')
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