/*
OAuth2RedirectHandler.jsx
소셜 로그인(OAuth 2.0) 성공 후 리디렉션되는 페이지입니다.
사용자가 Google, Kakao 등에서 인증을 마치면 백엔드 서버는 이 페이지로 토큰과 사용자 정보를 실어서 보내줍니다.
이 페이지는 그 정보를 받아 브라우저에 저장하고 로그인 절차를 마무리합니다.

URL 파라미터로 전달된 token과 email을 추출
추출한 정보를 localStorage에 저장하여 로그인 상태 유지
사용자를 메인 페이지(/)로 리디렉션
 */


import React, { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

const OAuth2RedirectHandler = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get("token");
    const email = searchParams.get("email");

    if (token && email) { // 💡 토큰과 이메일이 모두 있는지 확인
      // 토큰을 브라우저의 로컬 스토리지에 저장합니다.
      localStorage.setItem("token", token);

      localStorage.setItem("userEmail", email); 
      
      console.log("로그인 성공! 토큰 및 이메일 저장 완료.");

      navigate("/");
    } else {
      console.error("소셜 로그인에 실패했습니다: 토큰 또는 이메일이 없습니다.");
      navigate("/featureauth");
    }
  }, [navigate, searchParams]);

  return <div>로그인 처리 중...</div>;
};

export default OAuth2RedirectHandler;