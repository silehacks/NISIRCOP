<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-75 flex justify-center items-center z-50">
    <div class="bg-[#052659] p-8 rounded-lg shadow-2xl w-full max-w-lg border-2 border-[#5483B3]">
      <h2 class="text-2xl font-bold text-[#C1E8FF] mb-6">Edit Incident</h2>
      <form @submit.prevent="save">
        <div class="mb-4">
          <label for="title" class="block text-[#7DA0CA] text-sm font-bold mb-2">Title</label>
          <input v-model="editableIncident.title" type="text" id="title" class="w-full bg-[#021024] text-[#C1E8FF] border border-[#5483B3] rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]">
        </div>
        <div class="mb-4">
          <label for="description" class="block text-[#7DA0CA] text-sm font-bold mb-2">Description</label>
          <textarea v-model="editableIncident.description" id="description" rows="4" class="w-full bg-[#021024] text-[#C1E8FF] border border-[#5483B3] rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"></textarea>
        </div>
        <div class="grid grid-cols-2 gap-4 mb-6">
          <div>
            <label for="priority" class="block text-[#7DA0CA] text-sm font-bold mb-2">Priority</label>
            <select v-model="editableIncident.priority" id="priority" class="w-full bg-[#021024] text-[#C1E8FF] border border-[#5483B3] rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]">
              <option>Low</option>
              <option>Medium</option>
              <option>High</option>
            </select>
          </div>
          <div>
            <label for="status" class="block text-[#7DA0CA] text-sm font-bold mb-2">Status</label>
            <select v-model="editableIncident.status" id="status" class="w-full bg-[#021024] text-[#C1E8FF] border border-[#5483B3] rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]">
              <option>Open</option>
              <option>InProgress</option>
              <option>Resolved</option>
              <option>Closed</option>
            </select>
          </div>
        </div>
        <div class="flex justify-end space-x-4">
          <button @click="$emit('close')" type="button" class="px-4 py-2 text-sm font-medium text-white bg-gray-600 rounded-md hover:bg-gray-700 transition-colors">Cancel</button>
          <button type="submit" class="px-4 py-2 text-sm font-medium text-white bg-[#5483B3] rounded-md hover:bg-[#7DA0CA] transition-colors">Save Changes</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue';

const props = defineProps({
  show: Boolean,
  incident: Object
});

const emits = defineEmits(['close', 'save']);

const editableIncident = ref({});

watch(() => props.incident, (newVal) => {
  editableIncident.value = { ...newVal };
}, { immediate: true, deep: true });

const save = () => {
  emits('save', editableIncident.value);
};
</script>