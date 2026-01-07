/*
Search.jsx
병원 검색 조건을 입력받는 컴포넌트입니다.
지역, 진료과 등을 선택하고 검색 버튼을 누르면 SearchResult 페이지로 이동시키는 역할을 합니다.

드롭다운 메뉴를 통한 지역 및 진료과 선택
사용자가 선택한 검색 조건을 URL 파라미터로 만들어 검색 결과 페이지로 전달
 */


import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; //
import { locations } from "../components/locations.js";
import { departments } from "../components/department.js";
import HospitalData from "../components/HospitalData.js";

const Search = () => {
  const navigate = useNavigate(); //

  const [selectedCity, setSelectedCity] = useState("");
  const [selectedDistrict, setSelectedDistrict] = useState("");
  const [selectedDepartment, setSelectedDepartment] = useState("");

  const handleCityChange = (e) => {
    setSelectedCity(e.target.value);
    setSelectedDistrict("");
  };

  const handleDistrictChange = (e) => {
    setSelectedDistrict(e.target.value);
  };

  const handleDepartmentChange = (e) => {
    setSelectedDepartment(e.target.value);
  };

  const handleSearch = () => {
    navigate("/search-result");
  };

  return (
    <div className="search-container">
      <h2 className="search-title">원하는 병원 찾기</h2>

      <div className="filter-container">
        <input
          type="text"
          placeholder="병원명 / 진료과 / 지역(상세 주소)"
          className="keyword-input"
        />

        <select value={selectedCity} onChange={handleCityChange}>
          <option value="">시 선택</option>
          {locations.map((loc) => (
            <option key={loc.city} value={loc.city}>
              {loc.city}
            </option>
          ))}
        </select>

        <select
          value={selectedDistrict}
          onChange={handleDistrictChange}
          disabled={!selectedCity}
        >
          <option value="">구 선택</option>
          {selectedCity &&
            locations
              .find((loc) => loc.city === selectedCity)
              ?.districts.map((gu) => (
                <option key={gu} value={gu}>
                  {gu}
                </option>
              ))}
        </select>

        <select value={selectedDepartment} onChange={handleDepartmentChange}>
          <option value="">진료과목 선택</option>
          {departments.map((dept) => (
            <option key={dept.id} value={dept.id}>
              {dept.name}
            </option>
          ))}
        </select>

        {/* ✅ 검색 버튼으로 라우팅 */}
        <button onClick={handleSearch} className="search-button">
          검색
        </button>
      </div>
    </div>
  );
};

export default Search;
