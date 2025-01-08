
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { configDefaults } from 'vitest/config'; // Import pour simplifier la config

export default defineConfig({
  plugins: [react()],
  server: {
    open: true,
  },
  resolve: {
    alias: {
      '@': '/src',
    },
  },
  envPrefix: ['VITE_', 'REACT_APP_'],
  
});
