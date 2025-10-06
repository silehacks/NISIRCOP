<template>
  <div>
    <label :for="id" class="block text-sm font-medium text-[#7DA0CA]">{{ label }}</label>
    <input
      :type="type"
      :id="id"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :required="required"
      class="w-full mt-1 px-4 py-2 bg-[#5483B3] bg-opacity-30 border border-[#5483B3] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7DA0CA]"
    />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string | number;
  label: string;
  id: string;
  type?: string;
  required?: boolean;
}>();
defineEmits(['update:modelValue']);
</script>