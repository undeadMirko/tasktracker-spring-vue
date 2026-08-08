<template>
  <div class="layout-container">
    <aside class="sidebar glass-card">
      <div class="logo">
        <h2>TaskTracker</h2>
      </div>
      <nav class="nav-links">
        <router-link to="/dashboard" class="nav-item">Panel de Control</router-link>
        <router-link to="/tasks" class="nav-item">Tareas</router-link>
      </nav>
      <div class="user-info">
        <p v-if="authStore.user">Bienvenido, {{ authStore.user.username }}</p>
        <p v-if="authStore.user" class="role-badge">
          {{ authStore.user.role === 'ROLE_ADMIN' ? 'Admin' : 'Usuario' }}
        </p>
        <button @click="handleLogout" class="btn btn-danger">Cerrar Sesión</button>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 280px;
  border-radius: 0;
  border-left: none;
  border-top: none;
  border-bottom: none;
  display: flex;
  flex-direction: column;
  padding: 2rem;
  z-index: 10;
}

.logo h2 {
  color: var(--primary);
  font-weight: 700;
  margin-bottom: 3rem;
  text-align: center;
}

.nav-links {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.nav-item {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
  padding: 0.75rem 1rem;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.nav-item:hover,
.router-link-active {
  background-color: rgba(99, 102, 241, 0.1);
  color: var(--primary);
}

.user-info {
  margin-top: auto;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-info p {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.role-badge {
  background: rgba(139, 92, 246, 0.2);
  color: var(--primary) !important;
  padding: 0.2rem 0.5rem;
  border-radius: var(--radius-full);
  display: inline-block;
  margin: 0 auto 1rem auto;
  font-size: 0.75rem !important;
  font-weight: 700;
  text-transform: uppercase;
}

.main-content {
  flex-grow: 1;
  padding: 3rem;
  overflow-y: auto;
  position: relative;
}
</style>
