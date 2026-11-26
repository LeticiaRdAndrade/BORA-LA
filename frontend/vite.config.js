import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Quando eu rodo "npm run dev", as chamadas /api vão para o Spring (porta 8080)
export default defineConfig({
  plugins: [react()],
  server: { proxy: { "/api": "http://localhost:8080" } },
});
