import { defineStore } from 'pinia';
import api from '../api/axios';
import router from '../router';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    token: localStorage.getItem('token') || null,
    loading: false,
    error: null as string | null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token
  },

  actions: {
    async login(credentials: any) {
      this.loading = true;
      this.error = null;
      try {
        const response = await api.post('/auth/signin', credentials);
        this.token = response.data.accessToken;
        this.user = {
          id: response.data.id,
          username: response.data.username,
          role: response.data.role
        };

        localStorage.setItem('token', this.token!);
        localStorage.setItem('user', JSON.stringify(this.user));

        router.push('/dashboard');
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Error al iniciar sesión';
      } finally {
        this.loading = false;
      }
    },

    async register(data: any) {
      this.loading = true;
      this.error = null;
      try {
        await api.post('/auth/signup', data);
        await this.login({ username: data.username, password: data.password });
      } catch (err: any) {
        this.error = err.response?.data?.message || 'Error en el registro';
      } finally {
        this.loading = false;
      }
    },

    logout() {
      this.user = null;
      this.token = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      router.push('/login');
    }
  }
});
