<template>
  <div class="tasks-page animate-fade-in">
    <header class="page-header">
      <div>
        <h1>Gestión de Tareas</h1>
        <p class="text-secondary">Administra, asigna y organiza el trabajo de tu equipo.</p>
      </div>
      <button @click="openModal()" class="btn btn-primary">+ Nueva Tarea</button>
    </header>

    <div class="filters glass-card animate-fade-in-delayed">
      <div class="form-group">
        <label>Estado</label>
        <select v-model="filters.status" @change="fetchTasks">
          <option value="">Todos</option>
          <option value="PENDING">Pendiente</option>
          <option value="IN_PROGRESS">En Progreso</option>
          <option value="DONE">Completada</option>
        </select>
      </div>
      <div class="form-group">
        <label>Prioridad</label>
        <select v-model="filters.priority" @change="fetchTasks">
          <option value="">Todas</option>
          <option value="LOW">Baja</option>
          <option value="MEDIUM">Media</option>
          <option value="HIGH">Alta</option>
        </select>
      </div>
      <div class="form-group">
        <label>Asignado A</label>
        <select v-model="filters.assigneeId" @change="fetchTasks">
          <option value="">Cualquiera</option>
          <option v-for="user in users" :key="user.id" :value="user.id">
            {{ user.username }}
          </option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="loading">Cargando tareas...</div>

    <div v-else class="tasks-grid">
      <div
        v-for="task in tasks"
        :key="task.id"
        class="task-card glass-card animate-fade-in-delayed"
      >
        <div class="task-header">
          <h3>{{ task.title }}</h3>
          <div class="task-actions">
            <button @click="openModal(task)" class="btn-icon">✎</button>
            <button
              v-if="authStore.user?.role === 'ROLE_ADMIN'"
              @click="deleteTask(task.id)"
              class="btn-icon text-danger"
            >
              ✖
            </button>
          </div>
        </div>
        <p class="task-desc">{{ task.description }}</p>
        <div class="task-footer">
          <div class="tags">
            <span :class="['badge', `badge-${task.status.toLowerCase().replace('_', '-')}`]">{{
              translateStatus(task.status)
            }}</span>
            <span class="priority">Pri: {{ translatePriority(task.priority) }}</span>
          </div>
          <div class="assignee-badge" v-if="task.assigneeId">
            <span class="avatar">{{
              getAssigneeName(task.assigneeId).charAt(0).toUpperCase()
            }}</span>
            <small>{{ getAssigneeName(task.assigneeId) }}</small>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content glass-card animate-fade-in">
        <h2>{{ editingTask ? 'Editar Tarea' : 'Nueva Tarea' }}</h2>
        <form @submit.prevent="saveTask">
          <div class="form-group">
            <label>Título</label>
            <input
              v-model="taskForm.title"
              required
              placeholder="Ej. Actualizar diseño de base de datos"
            />
          </div>
          <div class="form-group">
            <label>Descripción</label>
            <textarea
              v-model="taskForm.description"
              rows="3"
              placeholder="Detalles de la tarea..."
            ></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Estado</label>
              <select v-model="taskForm.status">
                <option value="PENDING">Pendiente</option>
                <option value="IN_PROGRESS">En Progreso</option>
                <option value="DONE">Completada</option>
              </select>
            </div>
            <div class="form-group">
              <label>Prioridad</label>
              <select v-model="taskForm.priority">
                <option value="LOW">Baja</option>
                <option value="MEDIUM">Media</option>
                <option value="HIGH">Alta</option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label>Asignar a</label>
            <select v-model="taskForm.assigneeId">
              <option value="">Sin asignar</option>
              <option v-for="user in users" :key="user.id" :value="user.id">
                {{ user.username }}
              </option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="btn">Cancelar</button>
            <button type="submit" class="btn btn-primary">Guardar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '../api/axios';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const tasks = ref<any[]>([]);
const users = ref<any[]>([]);
const loading = ref(true);
const isModalOpen = ref(false);
const editingTask = ref<any>(null);

const filters = ref({
  status: '',
  priority: '',
  assigneeId: ''
});

const taskForm = ref({
  title: '',
  description: '',
  status: 'PENDING',
  priority: 'MEDIUM',
  assigneeId: ''
});

const fetchUsers = async () => {
  try {
    const res = await api.get('/users');
    users.value = res.data;
  } catch (err) {
    console.error(err);
  }
};

const fetchTasks = async () => {
  loading.value = true;
  try {
    const params = new URLSearchParams();
    if (filters.value.status) params.append('status', filters.value.status);
    if (filters.value.priority) params.append('priority', filters.value.priority);
    if (filters.value.assigneeId) params.append('assigneeId', filters.value.assigneeId);

    const res = await api.get(`/tasks?${params.toString()}`);
    tasks.value = res.data;
  } catch (err) {
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const openModal = (task?: any) => {
  if (task) {
    editingTask.value = task;
    taskForm.value = { ...task, assigneeId: task.assigneeId || '' };
  } else {
    editingTask.value = null;
    taskForm.value = {
      title: '',
      description: '',
      status: 'PENDING',
      priority: 'MEDIUM',
      assigneeId: ''
    };
  }
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const saveTask = async () => {
  try {
    // Convert empty string to null for assigneeId
    const payload = { ...taskForm.value };
    if (!payload.assigneeId) delete payload.assigneeId;

    if (editingTask.value) {
      await api.put(`/tasks/${editingTask.value.id}`, payload);
    } else {
      await api.post('/tasks', payload);
    }
    closeModal();
    fetchTasks();
  } catch (err) {
    console.error(err);
  }
};

const deleteTask = async (id: number) => {
  if (confirm('¿Estás seguro de que deseas eliminar esta tarea? (Solo Administradores)')) {
    try {
      await api.delete(`/tasks/${id}`);
      fetchTasks();
    } catch (err) {
      alert('Error: No tienes permisos para eliminar tareas.');
      console.error(err);
    }
  }
};

const getAssigneeName = (id: number) => {
  const u = users.value.find((u) => u.id === id);
  return u ? u.username : 'Unknown';
};

const translateStatus = (s: string) => {
  if (s === 'PENDING') return 'Pendiente';
  if (s === 'IN_PROGRESS') return 'En Progreso';
  if (s === 'DONE') return 'Completada';
  return s;
};

const translatePriority = (p: string) => {
  if (p === 'LOW') return 'Baja';
  if (p === 'MEDIUM') return 'Media';
  if (p === 'HIGH') return 'Alta';
  return p;
};

onMounted(() => {
  fetchUsers();
  fetchTasks();
});
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.filters {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding: 1.5rem;
  flex-wrap: wrap;
}

.filters .form-group {
  margin-bottom: 0;
  min-width: 200px;
  flex: 1;
}

.tasks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}

.task-card {
  display: flex;
  flex-direction: column;
  padding: 1.5rem;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.task-header h3 {
  margin: 0;
  font-size: 1.25rem;
  word-break: break-word;
}

.task-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  background: rgba(255, 255, 255, 0.05);
  border: none;
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 1rem;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}
.btn-icon:hover {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.15);
  transform: scale(1.1);
}
.text-danger:hover {
  color: white;
  background: var(--danger);
}

.task-desc {
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin-bottom: 1.5rem;
  flex-grow: 1;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid var(--glass-border);
  padding-top: 1rem;
  margin-top: auto;
}

.tags {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.priority {
  font-size: 0.85rem;
  color: var(--text-secondary);
  font-weight: 600;
  text-transform: uppercase;
}

.assignee-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.05);
  padding: 0.25rem 0.5rem;
  border-radius: var(--radius-full);
}

.avatar {
  background: var(--primary);
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: bold;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(11, 15, 25, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  backdrop-filter: blur(8px);
}

.modal-content {
  width: 100%;
  max-width: 500px;
}

.form-row {
  display: flex;
  gap: 1rem;
}
.form-row .form-group {
  flex: 1;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}
</style>
