// pages/FeatureAuth.jsx
/*
FeatureAuth.jsx
사용자의 로그인을 처리하는 페이지
아이디(이메일)와 비밀번호를 입력받아 로그인을 시도하며, 소셜 로그인(Google, Kakao)기능도 제공합니다.

일반 로그인 폼 제공 및 /api/auth/login 으로 로그인 요청
로그인 성공 시, 서버로부터 받은 JWT 토큰을 localStorage에 저장하고 메인 페이지로 이동
회원가입 페이지로 이동하는 링크 제공
 */


import React, { useState } from "react";
// ⭐️ 수정: 직접 'axios' 대신 우리가 만든 인스턴스를 가져옵니다.
import api from "../api/axios"; 
import "../components/FeatureAuth.css";
import { useNavigate } from "react-router-dom";

const FeatureAuth = () => {
  const navigate = useNavigate();

  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const handleJoinClick = () => {
    navigate("/join");
  };

  const handleLogin = async () => {
    if (!id || !password) {
      setErrorMessage("아이디와 비밀번호를 모두 입력해주세요.");
      return;
    }

    try {
      const response = await api.post("/auth/login", {
        email: id,
        password,
      });

      if (response.status === 200 && response.data.token) {
        const { token } = response.data;
        localStorage.setItem("token", token);
        alert("로그인에 성공했습니다!");
        navigate("/");
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        setErrorMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
      } else {
        setErrorMessage(
          "로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        );
      }
      console.error("Login error:", error);
    }
  };

  return (
    <div className="feature-auth-container">
      <header className="feature-auth-header">
        <div className="logo">닥큐</div>
      </header>

      <h2 className="feature-auth-title">로그인 및 회원가입</h2>

      <div className="feature-auth-box">
        <div className="feature-auth-left">
          <div className="feature-auth-input-group">
            <label>아이디(ID)</label>
            <input
              type="text"
              value={id}
              onChange={(e) => setId(e.target.value)}
              placeholder="아이디를 입력하세요"
            />
          </div>
          <div className="feature-auth-input-group">
            <label>비밀번호(PW)</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
            />
          </div>

          {errorMessage && <p className="error-message">{errorMessage}</p>}
          
          <button className="feature-auth-login-btn" onClick={handleLogin}>
            로그인
          </button>
          
          <div className="feature-auth-social">
            <button
              onClick={() =>
                (window.location.href =
                  "http://localhost:8080/oauth2/authorization/google")
              }
            >
              <img
                src="https://cdn.jsdelivr.net/gh/simple-icons/simple-icons/icons/google.svg"
                alt="Google"
                width="15"
                height="15"
                style={{ marginRight: "8px", verticalAlign: "middle" }}
              />
              Google로 로그인
            </button>
            <button
              onClick={() =>
                (window.location.href =
                  "http://localhost:8080/oauth2/authorization/kakao")
              }
            >
              <img
                src="https://cdn.jsdelivr.net/gh/simple-icons/simple-icons/icons/kakaotalk.svg"
                alt="Kakao"
                width="15"
                height="15"
                style={{ marginRight: "8px", verticalAlign: "middle" }}
              />
              Kakao로 로그인
            </button>
          </div>
        </div>

        <div className="feature-auth-divider" />

        <div className="feature-auth-right">
          <button
            className="feature-auth-register-btn"
            onClick={handleJoinClick}
          >
            회원가입
          </button>
        </div>
      </div>

      <div className="feature-auth-footer">
        <ul>
          <li>
            ● 닥큐 홈페이지의 회원이 되시면 다양한 정보와 맞춤 서비스를 이용하실
            수 있습니다.
          </li>
          <li>
            ● 비밀번호는 주기적으로 변경하고 타인에게 노출되지 않도록 주의하시기
            바랍니다.
          </li>
          <li>● 로그인 후 모든 정보는 암호화하여 전송됩니다.</li>
        </ul>
      </div>
    </div>
  );
};
export default FeatureAuth;