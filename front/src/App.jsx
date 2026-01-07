/*
App.jsx
애플리케이션의 메인 레이아웃 및 라우팅을 총괄합니다.
모든 페이지 컴포넌트들을 import하고,react-router-dom 의 Routes와 Route를 사용해 각 URL 경로에 맞는 페이지를 연결해줍니다.
또한, 인증 실패 시(세션 만료 등) 로그인 페이지로 이동시키는 전역 이벤트 리스너를 설정합니다.
**핵심 기능**
URL 경로에 따른 페이지 컴포넌트 매핑 (예: / -> /MainPage, /mypage -> MyPage)
인증 실패(`unauthorized`) 이벤트를 감지하여 로그인 페이지(/featureauth)로 자동 리디렉션
*/



import "./App.css";
import { Routes, Route, useNavigate } from "react-router-dom";
import React, { useEffect } from "react";

// 각 페이지 컴포넌트 import
import MainPage from "./pages/MainPage";
import FeatureAuth from "./pages/FeatureAuth";
import FeatureAuthJoin from "./pages/FeatureAuthJoin";
import SearchResult from "./pages/SearchResult";
import Reservation from "./pages/Reservation";
import ReservationRt from "./pages/ReservationRt";
import Review from "./pages/Review";
import MyPage from "./pages/MyPage";
import Notfound from "./pages/Notfound";
import OAuth2RedirectHandler from "./pages/OAuth2RedirectHandler";

function App() {
  const navigate = useNavigate();

  useEffect(() => {
    const handleUnauthorized = () => {
      console.log("세션 만료, 로그인 페이지로 이동합니다."); // 개발자 확인용 로그
      navigate("/featureauth");
    };

    window.addEventListener("unauthorized", handleUnauthorized);

    return () => {
      window.removeEventListener("unauthorized", handleUnauthorized);
    };
  }, [navigate]);

  return (
    <>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/FeatureAuth" element={<FeatureAuth />} />
        <Route path="/FeatureAuthJoin" element={<FeatureAuthJoin />} />
        <Route path="/join" element={<FeatureAuthJoin />} />
        <Route path="/search" element={<SearchResult />} />
        <Route path="/Reservation" element={<Reservation />} />
        <Route path="/ReservationRt" element={<ReservationRt />} />
        <Route path="/Review" element={<Review />} />
        <Route path="/MyPage" element={<MyPage />} />
        <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
        <Route path="*" element={<Notfound />} />
      </Routes>
    </>
  );
}

export default App;