/*
MainPage.jsx
웹사이트의 대문 역할을 하는 메인 페이지
=> 사용자에게 병원 검색 기능을 제공하며, 로그인 상태에 따라 다른 메뉴(로그인/로그아웃, 마이페이지)를 보여줍니다.

기능
-지역(시/도, 시/군/구), 진료과, 병원명 키워드를 이용한 병원 검색 UI 제공
-로그인 여부 확인 및 조건부 UI 렌더링
-검색 조건과 함께 검색 결과 페이지(/search)로 이동
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../components/MainPage.css";
import { locations } from "../components/locations.js";
import { departments } from "../components/department.js";
import mainPageImage from "../assets/MainPageill.png";

function MainPage() {
  const navigate = useNavigate();

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [selectedSido, setSelectedSido] = useState("");
  const [selectedSigunguCode, setSelectedSigunguCode] = useState("");
  const [selectedDepartmentCode, setSelectedDepartmentCode] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      setIsLoggedIn(true);
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    alert("로그아웃되었습니다.");
    navigate("/");
  };

  const handleLoginClick = () => {
    navigate("/featureauth");
  };

  const handleMyPageClick = () => {
    navigate("/mypage");
  };

  const handleSearch = () => {
    const params = new URLSearchParams();

    if (selectedSido) {
      const sidoCodeValue = locations[selectedSido]?.sidoCode;
      if (sidoCodeValue) {
        params.append('sidoCode', sidoCodeValue);
      }
    }

    if (selectedSigunguCode) {
      params.append('sgguCode', selectedSigunguCode);
    }

    if (selectedDepartmentCode) {
      const department = departments.find(d => d.code === selectedDepartmentCode);
      if (department) {
        params.append('departmentCode', department.name);
      }
    }
    // ------------------------------------

    if (searchTerm.trim()) {
      params.append('name', searchTerm.trim());
    }

    if (Array.from(params.keys()).length === 0) {
      alert("검색할 조건을 하나 이상 선택 또는 입력해주세요.");
      return;
    }

    navigate(`/search?${params.toString()}`);
  };

  return (
    <div className="main-container">
      {/* 헤더 부분은 이전과 동일 */}
      <header className="main-header">
        <div className="logo" onClick={() => navigate("/")} style={{ cursor: "pointer" }}>
          닥큐
        </div>
        <div className="header-links">
          {isLoggedIn ? (
            <>
              <a onClick={handleMyPageClick} className="mypage-link" style={{ cursor: "pointer" }}>
                마이페이지
              </a>
              <button onClick={handleLogout} className="logout-btn">
                로그아웃
              </button>
            </>
          ) : (
            <a onClick={handleLoginClick} className="login-link" style={{ cursor: "pointer" }}>
              로그인/회원가입
            </a>
          )}
        </div>
      </header>
      
      {/* 메인 콘텐츠 부분은 이전과 동일 */}
      <main className="main-body">
        <div className="top-section">
          <img src={mainPageImage} alt="main illustration" className="MainPageill" />
          <div className="speech-bubble">
            원하는 병원과 진료과를 <br />
            검색하고 예약까지 한 번에!
          </div>
        </div>

        <div className="bottom">
          <h2 className="search-title">어떤 병원을 찾으시나요?</h2>
          <div className="search-box">
            <select
              className="category-select"
              value={selectedSido}
              onChange={(e) => {
                setSelectedSido(e.target.value);
                setSelectedSigunguCode("");
              }}
            >
              <option value="">시/도 선택</option>
              {Object.keys(locations).map((sidoName) => (
                <option key={sidoName} value={sidoName}>
                  {sidoName}
                </option>
              ))}
            </select>

            <select
              className="category-select"
              value={selectedSigunguCode}
              onChange={(e) => setSelectedSigunguCode(e.target.value)}
              disabled={!selectedSido}
            >
              <option value="">시/군/구 선택</option>
              {selectedSido &&
                locations[selectedSido].sigungu.map((sigungu) => (
                  <option key={sigungu.code} value={sigungu.code}>
                    {sigungu.name}
                  </option>
                ))}
            </select>
            
            <select
              className="category-select"
              value={selectedDepartmentCode}
              onChange={(e) => setSelectedDepartmentCode(e.target.value)}
            >
              <option value="">진료과 선택</option>
              {departments.map((dept) => (
                <option key={dept.code} value={dept.code}>
                  {dept.name}
                </option>
              ))}
            </select>

            <input
              type="text"
              placeholder="병원 이름을 검색하세요"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />

            <button className="search-start" onClick={handleSearch}>
              검색
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}

export default MainPage;