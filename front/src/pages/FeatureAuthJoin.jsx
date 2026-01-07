/*
FeatureAuthJoin.jsx
신규 사용자의 회원가입을 처리하는 페이지입니다.
아이디, 비밀번호, 이름, 이메일 등 회원가입에 필요한 정보를 입력받는 폼을 제공하며, 이메일 인증 기능도 포함됨

회원가입 폼 및 입력값에 대한 실시간 유효성 검사 (예: 아이디 길이, 비밀번호 복잡도)
입력된 사용자 정보를 백엔드 API로 전송하여 회원 등록
 */

import React, { useState } from "react";
// ⭐️ 1. import 문을 'api'로 변경합니다.
import api from "../api/axios";
import { useNavigate } from "react-router-dom";
import "../components/FeatureAuthJoin.css";

const FeatureAuthJoin = () => {
  const navigate = useNavigate();

  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [name, setName] = useState("");
  const [residentNumber, setResidentNumber] = useState("");
  const [address, setAddress] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [idMessage, setIdMessage] = useState("");
  const [passwordMessage, setPasswordMessage] = useState("");
  const [emailMessage, setEmailMessage] = useState("");
  const [verifyMessage, setVerifyMessage] = useState("");
  const [signUpMessage, setSignUpMessage] = useState("");

  const handleIdChange = (e) => {
    const value = e.target.value;
    setId(value);
    if (value.length >= 8) {
      setIdMessage("사용 가능한 아이디입니다.");
    } else {
      setIdMessage("아이디는 최소 8자 이상이어야 합니다.");
    }
  };
  const handlePasswordChange = (e) => {
    const value = e.target.value;
    setPassword(value);
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (regex.test(value)) {
      setPasswordMessage("안전한 비밀번호입니다.");
    } else {
      setPasswordMessage("대소문자, 숫자를 포함한 8자 이상 입력하세요.");
    }
  };

  const sendVerificationEmail = async () => {
    try {
      // ⭐️ 2. 'axios.post'를 'api.post'로 변경하고 URL 경로를 수정합니다.
      const response = await api.post("/email/send", { email });
      if (response.status === 200) {
        setEmailMessage("인증 이메일이 전송되었습니다.");
      }
    } catch (error) {
      setEmailMessage(
        "이메일 전송 실패: " + (error.response?.data?.message || "서버 오류")
      );
    }
  };

  const verifyCode = async () => {
    try {
      // ⭐️ 3. 'axios.post'를 'api.post'로 변경하고 URL 경로를 수정합니다.
      const response = await api.post("/email/verify", {
        email,
        code: verificationCode,
      });
      if (response.status === 200) {
        setVerifyMessage("이메일 인증이 완료되었습니다.");
      } else {
        setVerifyMessage("인증번호가 올바르지 않습니다.");
      }
    } catch (error) {
      setVerifyMessage(
        "인증 실패: " + (error.response?.data?.message || "서버 오류")
      );
    }
  };

  const handleSignUp = async () => {
    if (
      !id ||
      !password ||
      !name ||
      !email ||
      !verifyMessage.includes("완료")
    ) {
      setSignUpMessage("모든 필수 정보를 입력하고 이메일 인증을 완료해주세요.");
      return;
    }

    try {
      // ⭐️ 4. 'axios.post'를 'api.post'로 변경하고 URL 경로를 수정합니다.
      const response = await api.post("/auth/join", {
        id,
        password,
        name,
        residentNumber,
        address,
        phoneNumber,
        email,
      });

      if (response.status === 200) {
        alert(
          "회원가입이 성공적으로 완료되었습니다! 로그인 페이지로 이동합니다."
        );
        navigate("/FeatureAuth");
      }
    } catch (error) {
      setSignUpMessage(
        "회원가입 실패: " + (error.response?.data?.message || "서버 오류")
      );
    }
  };

  // ... (return 부분은 동일)
  return (
    <>
      <header className="main-header">
        <div className="logo">닥큐</div>
      </header>
      <div className="signup-wrapper">
        <div className="signup-container">
          <h2 className="signup-title">회원가입</h2>

          <label>아이디 (최소 8자)</label>
          <input
            type="text"
            value={id}
            onChange={handleIdChange}
            placeholder="아이디 입력"
          />
          <p className="validation-msg">{idMessage}</p>

          <label>비밀번호</label>
          <input
            type="password"
            value={password}
            onChange={handlePasswordChange}
            placeholder="비밀번호 입력"
          />
          <p className="validation-msg">{passwordMessage}</p>

          <label>이름 (실명)</label>
          {/* === 수정된 입력 필드 === */}
          <input
            type="text"
            placeholder="이름 입력"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />

          <label>주민등록번호</label>
          {/* === 수정된 입력 필드 === */}
          <input
            type="text"
            placeholder="주민등록번호 입력"
            value={residentNumber}
            onChange={(e) => setResidentNumber(e.target.value)}
          />

          <label>주소</label>
          {/* === 수정된 입력 필드 === */}
          <input
            type="text"
            placeholder="주소 입력"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
          />

          <label>전화번호</label>
          {/* === 수정된 입력 필드 === */}
          <input
            type="text"
            placeholder="전화번호 입력"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
          />

          <div className="email-verify-group">
            {/* ... 이메일 인증 부분은 기존과 동일 ... */}
            <label>이메일 본인인증</label>
            <div className="email-section">
              <input
                type="email"
                placeholder="example@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <button className="btn" onClick={sendVerificationEmail}>
                인증
              </button>
            </div>
            <p className="email-msg">{emailMessage}</p>

            <div className="verify-section">
              <input
                type="text"
                placeholder="인증번호"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
              />
              <button
                className="btn"
                onClick={verifyCode}
                disabled={verificationCode.length !== 6}
              >
                확인
              </button>
            </div>
            <p className="email-msg">{verifyMessage}</p>
          </div>

          {/* === 수정된 버튼과 에러 메시지 표시 === */}
          <button className="signup-btn" onClick={handleSignUp}>
            회원가입
          </button>
          <p className="validation-msg">{signUpMessage}</p>
        </div>
      </div>
    </>
  );
};
export default FeatureAuthJoin;
