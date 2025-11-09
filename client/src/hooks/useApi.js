import { useMemo } from 'react';
import axios from 'axios';

export function useApi() {
  const client = useMemo(() => {
    const instance = axios.create({
      baseURL: import.meta.env.VITE_API_URL || 'http://localhost:4000/api'
    });

    instance.interceptors.request.use((config) => {
      const token = localStorage.getItem('nisircop:token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    return instance;
  }, []);

  return client;
}
