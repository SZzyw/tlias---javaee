import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue({
    template: {
      compilerOptions: {
        isCustomElement: (tag) => tag === 'open-chat-widget'
      }
    }
  })],
  resolve: {
    dedupe: ['react', 'react-dom', '@ai-sdk/react', 'ai'],
    alias: [
      { find: '@', replacement: fileURLToPath(new URL('./src', import.meta.url)) },
      { find: '#minpath', replacement: fileURLToPath(new URL('./node_modules/vfile/lib/minpath.browser.js', import.meta.url)) },
      { find: '#minproc', replacement: fileURLToPath(new URL('./node_modules/vfile/lib/minproc.browser.js', import.meta.url)) },
      { find: '#minurl', replacement: fileURLToPath(new URL('./node_modules/vfile/lib/minurl.browser.js', import.meta.url)) },
    ]
  },
  server: {
    proxy: {
      '/api/chat': {
        target: 'http://localhost:8787',
        secure: false,
        changeOrigin: true,
      },
      '/head': {
        target: 'http://localhost:8080',
        secure: false,
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:8080',
        secure: false,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      }
    }
  }
})
