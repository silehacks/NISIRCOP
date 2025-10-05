<template>
  <div class="p-6">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-xl font-semibold text-black">Users</h2>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="md:col-span-2 bg-white p-4 rounded shadow">
        <table class="min-w-full bg-white">
          <thead>
            <tr class="text-left">
              <th class="px-4 py-2">ID</th>
              <th class="px-4 py-2">Username</th>
              <th class="px-4 py-2">Role</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in users" :key="u.id" class="border-t">
              <td class="px-4 py-2">{{ u.id }}</td>
              <td class="px-4 py-2">{{ u.username }}</td>
              <td class="px-4 py-2">{{ u.role }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="bg-white p-4 rounded shadow">
        <h3 class="font-semibold mb-2">Create User</h3>
        <form @submit.prevent="createNew">
          <div class="mb-2">
            <label class="block text-sm">Username</label>
            <input v-model="form.username" class="w-full px-2 py-1 border rounded" />
          </div>
          <div class="mb-2">
            <label class="block text-sm">Password</label>
            <input v-model="form.password" type="password" class="w-full px-2 py-1 border rounded" />
          </div>
                <div class="mb-2">
                  <label class="block text-sm">Role</label>
                  <select v-model="form.role" class="w-full px-2 py-1 border rounded">
                    <option>SUPER_USER</option>
                    <option>POLICE_STATION</option>
                    <option>OFFICER</option>
                  </select>
                </div>
                <div class="mb-2">
                  <label class="block text-sm">Email</label>
                  <input v-model="form.email" type="email" class="w-full px-2 py-1 border rounded" />
                </div>
                <div class="mb-2">
                  <label class="block text-sm">First name</label>
                  <input v-model="form.firstName" class="w-full px-2 py-1 border rounded" />
                </div>
                <div class="mb-2">
                  <label class="block text-sm">Last name</label>
                  <input v-model="form.lastName" class="w-full px-2 py-1 border rounded" />
                </div>
                <div class="mb-2">
                  <label class="block text-sm">Phone</label>
                  <input v-model="form.phone" class="w-full px-2 py-1 border rounded" />
                </div>
                <div class="mb-2">
                  <label class="block text-sm">Badge number</label>
                  <input v-model="form.badgeNumber" class="w-full px-2 py-1 border rounded" />
                </div>
                <div class="mb-2">
                  <label class="block text-sm">Boundary (optional)</label>
                  <input v-model="form.boundary" class="w-full px-2 py-1 border rounded" />
                </div>
          <div class="flex justify-end">
            <button type="submit" class="px-3 py-1 bg-[#1478f0] text-white rounded">Create</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as UsersService from '../services/users.service'

const users = ref<any[]>([])
const form = ref({ username: '', password: '', role: 'POLICE_STATION', email: '', firstName: '', lastName: '', phone: '', badgeNumber: '', boundary: null })

const load = async () => {
  try {
    const r = await UsersService.getUsers()
    users.value = r.data
  } catch (e) { users.value = [] }
}

const createNew = async () => {
  try {
    await UsersService.createUser(form.value)
    form.value = { username: '', password: '', role: 'USER' }
    await load()
  } catch (e) { console.error(e) }
}

onMounted(load)
</script>

<style scoped>
</style>
