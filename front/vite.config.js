/*
vite.config.js
프론트엔드 개발 서버 및 빌드 도구인 Vite의 설정 파일
개발 환경에서 발생하는 CORS 문제를 해결하기 위해 프록시 설정을 포함하고 있습니다.

프론트엔드에서 /api 로 시작하는 모든 요청을 백엔드 서버 주소(http://localhost:8080)로 대신 전달해줍니다.
 */

import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});