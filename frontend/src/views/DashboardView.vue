<template>
  <div class="dashboard animate-fade-in">
    <header class="page-header">
      <h1>Panel de Control</h1>
      <p class="text-secondary">Bienvenido. Aquí tienes un resumen de tus tareas.</p>
    </header>

    <div v-if="loading" class="loading">Cargando métricas...</div>

    <div v-else>
      <div class="metrics-grid animate-fade-in-delayed">
        <div class="metric-card glass-card">
          <h3>Total Tareas</h3>
          <div class="metric-value">{{ metrics.total }}</div>
        </div>

        <div class="metric-card glass-card">
          <h3>Pendientes</h3>
          <div class="metric-value pending">{{ metrics.pending }}</div>
        </div>

        <div class="metric-card glass-card">
          <h3>En Progreso</h3>
          <div class="metric-value in-progress">{{ metrics.inProgress }}</div>
        </div>

        <div class="metric-card glass-card">
          <h3>Completadas</h3>
          <div class="metric-value done">{{ metrics.done }}</div>
        </div>
      </div>

      <div class="charts-grid animate-fade-in-delayed">
        <div class="chart-container glass-card">
          <h3>Distribución de Estados</h3>
          <div class="chart-wrapper">
            <Pie v-if="statusChartData" :data="statusChartData" :options="chartOptions" />
          </div>
        </div>

        <div class="chart-container glass-card">
          <h3>Distribución de Prioridades</h3>
          <div class="chart-wrapper">
            <Bar v-if="priorityChartData" :data="priorityChartData" :options="chartOptions" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import api from '../api/axios';

import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js';
import { Pie, Bar } from 'vue-chartjs';

ChartJS.register(Title, Tooltip, Legend, ArcElement, BarElement, CategoryScale, LinearScale);

const metrics = ref({
  total: 0,
  pending: 0,
  inProgress: 0,
  done: 0,
  high: 0,
  medium: 0,
  low: 0
});
const loading = ref(true);

const fetchMetrics = async () => {
  try {
    const response = await api.get('/tasks/dashboard');
    metrics.value = response.data;
  } catch (error) {
    console.error('Failed to fetch metrics', error);
  } finally {
    loading.value = false;
  }
};

const statusChartData = computed(() => {
  if (loading.value) return null;
  return {
    labels: ['Pendiente', 'En Progreso', 'Completada'],
    datasets: [
      {
        backgroundColor: [
          'rgba(245, 158, 11, 0.8)',
          'rgba(6, 182, 212, 0.8)',
          'rgba(16, 185, 129, 0.8)'
        ],
        borderColor: ['#f59e0b', '#06b6d4', '#10b981'],
        borderWidth: 1,
        data: [metrics.value.pending, metrics.value.inProgress, metrics.value.done]
      }
    ]
  };
});

const priorityChartData = computed(() => {
  if (loading.value) return null;
  return {
    labels: ['Alta', 'Media', 'Baja'],
    datasets: [
      {
        label: 'Tareas por Prioridad',
        backgroundColor: [
          'rgba(244, 63, 94, 0.8)',
          'rgba(139, 92, 246, 0.8)',
          'rgba(148, 163, 184, 0.8)'
        ],
        borderColor: ['#f43f5e', '#8b5cf6', '#94a3b8'],
        borderWidth: 1,
        data: [metrics.value.high, metrics.value.medium, metrics.value.low]
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      labels: { color: '#ffffff' }
    }
  },
  scales: {
    x: { ticks: { color: '#94a3b8' } },
    y: { ticks: { color: '#94a3b8' } }
  }
};

onMounted(() => {
  fetchMetrics();
});
</script>

<style scoped>
.page-header {
  margin-bottom: 3rem;
}

.text-secondary {
  color: var(--text-secondary);
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.metric-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

.metric-card h3 {
  font-size: 1rem;
  color: var(--text-secondary);
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.metric-value {
  font-size: 3.5rem;
  font-weight: 700;
  line-height: 1;
}

.pending {
  color: var(--warning);
  text-shadow: 0 0 20px rgba(245, 158, 11, 0.4);
}
.in-progress {
  color: var(--accent);
  text-shadow: 0 0 20px rgba(6, 182, 212, 0.4);
}
.done {
  color: var(--success);
  text-shadow: 0 0 20px rgba(16, 185, 129, 0.4);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
}

.chart-container {
  display: flex;
  flex-direction: column;
}
.chart-container h3 {
  margin-bottom: 1.5rem;
  text-align: center;
  color: var(--text-secondary);
}

.chart-wrapper {
  position: relative;
  height: 300px;
  width: 100%;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: var(--text-secondary);
  font-size: 1.25rem;
}
</style>
