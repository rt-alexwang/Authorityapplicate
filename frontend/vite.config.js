import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  base: '/authority/',
  build: {
    outDir: '../backend/src/main/resources/static',
    emptyOutDir: true
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/authority/api': {
        target: 'http://192.168.82.225:8090',
        changeOrigin: true
      }
    }
  }
})
