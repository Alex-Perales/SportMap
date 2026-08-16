import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import {defineConfig} from 'vite';

export default defineConfig(() => {
  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '.'),
      },
    },
    server: {
      hmr: process.env.DISABLE_HMR !== 'true',
      watch: process.env.DISABLE_HMR === 'true' ? null : {},
      proxy: {
        // En producción el backend sirve /admin y el frontend en el mismo
        // dominio; en dev corren en puertos distintos, así que reenviamos
        // aquí para que el link "Iniciar Sesión" funcione igual.
        '/admin': 'http://localhost:8000',
        '/uploads': 'http://localhost:8000',
      },
    },
  };
});
