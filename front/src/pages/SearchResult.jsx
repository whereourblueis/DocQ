/*
SearchResult.jsx
병원 검색 결과를 목록 형태로 보여주는 페이지
MainPage 또는 다른 검색 경로를 통해 전달받은 검색 조건(URL 쿼리 파라미터)을 사용하여
백엔드에 병원 목록을 요청하고, 그 결과를 사용자에게 표시합니다.
 */

import React, { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import api from "../api/axios";
import "../components/SearchResult.css";

const SearchResult = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const [hospitals, setHospitals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const departmentName = searchParams.get("departmentCode");

  useEffect(() => {
    const queryParams = Object.fromEntries(searchParams.entries());

    const fetchHospitals = async () => {
      setLoading(true);
      setError("");
      try {
        const response = await api.get("/hospitals/search", {
          params: queryParams,
        });
        
        const hospitalData = response.data.content || response.data;

        if (Array.isArray(hospitalData)) {
          setHospitals(hospitalData);
        } else {
          setHospitals([]);
        }
      } catch (err) {
        if (err.response && err.response.status === 401) {
          return;
        }
        console.error("병원 정보 검색 실패:", err);
        setError("병원 정보를 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };
    
    if (Array.from(searchParams.keys()).length > 0) {
      fetchHospitals();
    } else {
      setLoading(false);
      setHospitals([]);
    }
  }, [searchParams]);

  const handleReservationClick = (hospital) => {
    if (!departmentName) {
      alert("진료과목을 지정하여 검색한 경우에만 예약이 가능합니다.\n메인 페이지에서 진료과목을 선택 후 다시 검색해주세요.");
      return;
    }
    // 예약 페이지로 이동할 때 전체 병원 데이터를 전달합니다.
    navigate("/Reservation", { state: { hospital, departmentName } });
  };
  
  if (loading) {
    return <div className="search-result-container"><h2>병원 정보를 검색 중입니다...</h2></div>;
  }

  if (error) {
    return <div className="search-result-container"><h2 className="error-message">{error}</h2></div>;
  }

  return (
    <div className="search-result-container">
      <header className="page-header">
        <h1 className="logo" onClick={() => navigate("/")}>닥큐</h1>
        <h2>{departmentName ? `'${departmentName}'` : '전체'} 검색 결과 ({hospitals.length}건)</h2>
      </header>
      
      <main>
        {hospitals.length > 0 ? (
          hospitals.map((hospital) => (
            // --- 💡 여기가 수정된 부분입니다 ---
            // key 값으로 백엔드에서 받은 고유 ID인 'generatedId' 또는 'id'를 사용합니다.
            <div key={hospital.generatedId || hospital.id} className="hospital-card">
              <div className="hospital-header">
                {/* 병원 이름을 'hospital.yadmNm'으로 변경 */}
                <h3>{hospital.yadmNm}</h3>
              </div>
              <div className="hospital-info">
                <p><span className="icon">📍</span> <span className="address">{hospital.addr || "주소 정보 없음"}</span></p>
                <p><span className="icon">📞</span> {hospital.telno || "전화번호 정보 없음"}</p>
              </div>
              <div className="hospital-actions">
                <button
                  className="reservation-btn"
                  onClick={() => handleReservationClick(hospital)}
                >
                  예약하기
                </button>
              </div>
            </div>
          ))
        ) : (
          <div className="no-results-card">
            <h3>검색된 병원이 없습니다.</h3>
            <p>다른 조건으로 다시 검색해보세요.</p>
          </div>
        )}
      </main>
    </div>
  );
};
export default SearchResult;