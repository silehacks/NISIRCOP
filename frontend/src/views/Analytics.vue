<template>
  <div class="text-[#C1E8FF]">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-3xl font-extrabold">Analytics Dashboard</h2>
    </div>

    <div v-if="analyticsStore.isLoading" class="text-center p-8">
      <p>Loading analytics data...</p>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <div class="bg-[#052659] p-6 rounded-lg shadow-lg">
        <h3 class="text-xl font-semibold mb-4 text-center">Incidents by Type</h3>
        <canvas id="incidentsByTypeChart"></canvas>
      </div>
      <div class="bg-[#052659] p-6 rounded-lg shadow-lg">
        <h3 class="text-xl font-semibold mb-4 text-center">Incidents by Priority</h3>
        <canvas id="incidentsByPriorityChart"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect, nextTick } from 'vue';
import { useAnalyticsStore } from '../stores/analytics.store';
import Chart from 'chart.js/auto';

const analyticsStore = useAnalyticsStore();
let typeChart: Chart | null = null;
let priorityChart: Chart | null = null;

onMounted(() => {
  analyticsStore.fetchAnalytics();
});

watchEffect(async () => {
  if (analyticsStore.incidentsByType.length > 0) {
    await nextTick();
    renderIncidentsByTypeChart();
  }
  if (analyticsStore.incidentsByPriority.length > 0) {
    await nextTick();
    renderIncidentsByPriorityChart();
  }
});

const chartColors = ['#C1E8FF', '#7DA0CA', '#5483B3', '#052659', '#021024'];

const renderIncidentsByTypeChart = () => {
  const ctx = document.getElementById('incidentsByTypeChart') as HTMLCanvasElement;
  if (!ctx) return;

  const data = {
    labels: analyticsStore.incidentsByType.map(item => item.name),
    datasets: [{
      data: analyticsStore.incidentsByType.map(item => item.count),
      backgroundColor: chartColors,
      borderColor: '#052659',
    }]
  };

  if (typeChart) {
    typeChart.data = data;
    typeChart.update();
  } else {
    typeChart = new Chart(ctx, {
      type: 'doughnut',
      data: data,
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'top',
            labels: {
              color: '#C1E8FF'
            }
          },
        }
      }
    });
  }
};

const renderIncidentsByPriorityChart = () => {
  const ctx = document.getElementById('incidentsByPriorityChart') as HTMLCanvasElement;
  if (!ctx) return;

  const data = {
    labels: analyticsStore.incidentsByPriority.map(item => item.name),
    datasets: [{
      label: 'Number of Incidents',
      data: analyticsStore.incidentsByPriority.map(item => item.count),
      backgroundColor: chartColors,
      borderColor: '#052659',
      borderWidth: 1
    }]
  };

  if (priorityChart) {
    priorityChart.data = data;
    priorityChart.update();
  } else {
    priorityChart = new Chart(ctx, {
      type: 'bar',
      data: data,
      options: {
        scales: {
          y: {
            beginAtZero: true,
            ticks: { color: '#C1E8FF' },
            grid: { color: 'rgba(193, 232, 255, 0.2)' }
          },
          x: {
            ticks: { color: '#C1E8FF' },
            grid: { color: 'rgba(193, 232, 255, 0.2)' }
          }
        },
        plugins: {
          legend: {
            display: false
          },
        }
      }
    });
  }
};
</script>