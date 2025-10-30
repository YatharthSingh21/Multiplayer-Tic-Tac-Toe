import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// ✅ Polyfill for SockJS `global` reference
export default defineConfig({
  plugins: [react()],
  define: {
    global: "window",
  },
});
