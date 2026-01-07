/*
axios.js
백엔드 서버와의 통신(API 요청)을 전담하는 설정 파일입니다.
axios 라이브러리의 인스턴스를 생성하여 API 요청에 대한 공통 설정을 관리합니다. 이를 통해 모든 API 요청을 일관되게 처리할 수 있습니다.

요청 인터셉터(Request Interceptor): API 요청을 보내기 직전에 localStorage에서 JWT 토큰을 가져와 Authorization 헤더에 자동으로 추가합니다.
응답 인터셉터(Response Interceptor): API 응답 상태가 401 Unauthorized (인증 실패)일 경우, 토큰을 삭제하고
                                  unauthorized 이벤트를 발생시켜 App.jsx가 로그인 페이지로 이동시키도록 합니다.
 */


import axios from "axios";

// 1. Axios 인스턴스 생성
const api = axios.create({
  baseURL: "/api",
});

// 2. 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 3. 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      
      if (error.config.url !== '/auth/login') {
        localStorage.removeItem("token");
        window.dispatchEvent(new Event("unauthorized"));
        return new Promise(() => {});
      }
    }

    return Promise.reject(error);
  }
);

export default api;