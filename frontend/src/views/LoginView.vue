<template>
  <div class="auth-container animate-fade-in">
    <div class="auth-card glass-card">
      <h1 class="auth-title">Bienvenido</h1>
      <p class="auth-subtitle">Inicia sesión en tu cuenta de TaskTracker</p>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="username">Usuario</label>
          <input
            type="text"
            id="username"
            v-model="username"
            required
            placeholder="Ingresa tu usuario"
          />
        </div>

        <div class="form-group">
          <label for="password">Contraseña</label>
          <input
            type="password"
            id="password"
            v-model="password"
            required
            placeholder="Ingresa tu contraseña"
          />
        </div>

        <div v-if="authStore.error" class="error-msg">{{ authStore.error }}</div>

        <button type="submit" class="btn btn-primary btn-block" :disabled="authStore.loading">
          {{ authStore.loading ? 'Iniciando...' : 'Iniciar Sesión' }}
        </button>
      </form>

      <p class="auth-footer">
        ¿No tienes una cuenta? <router-link to="/register" class="link">Regístrate</router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const username = ref('');
const password = ref('');

const handleLogin = async () => {
  await authStore.login({ username: username.value, password: password.value });
};
</script>

<style scoped>
.auth-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 2rem;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  padding: 3rem 2.5rem;
}

.auth-title {
  text-align: center;
  margin-bottom: 0.5rem;
}

.auth-subtitle {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: 2rem;
  font-size: 0.95rem;
}

.btn-block {
  width: 100%;
  margin-top: 1rem;
}

.error-msg {
  color: var(--danger);
  font-size: 0.875rem;
  margin-bottom: 1rem;
  text-align: center;
  background: rgba(239, 68, 68, 0.1);
  padding: 0.5rem;
  border-radius: var(--radius-md);
}

.auth-footer {
  margin-top: 2rem;
  text-align: center;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.link {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  transition: color var(--transition-fast);
}

.link:hover {
  color: var(--primary-hover);
}
</style>
