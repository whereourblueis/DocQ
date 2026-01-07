/*
Reservation.jsx
특정 병원의 진료를 예약하는 페이지
사용자가 병원, 날짜를 선택하면 예약 가능한 시간 목록을 보여주고, 최종적으로 예약을 확정하는 기능을 수행합니다.

날짜 선택 시, /api/reservations/booked-times API를 호출하여 해당 날짜에 이미 예약된 시간을 조회
예약 가능한 시간만 사용자에게 노출
선택된 예약 정보를 /api/reservations로 전송하여 예약 생성
 */

import React, { useState, useEffect, useMemo } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import '../components/Reservation.css';

const Reservation = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { hospital, departmentName } = location.state || {};

  const [selectedDate, setSelectedDate] = useState('');
  const [selectedTime, setSelectedTime] = useState('');
  const [bookedTimes, setBookedTimes] = useState([]); // [추가] 예약된 시간 목록 상태
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [timeLoading, setTimeLoading] = useState(false); // [추가] 시간 로딩 상태

  useEffect(() => {
    if (selectedDate && hospital) {
      const fetchBookedTimes = async () => {
        setTimeLoading(true);
        setError('');
        try {
          const response = await api.get('/reservations/booked-times', {
            params: {
              hospitalId: hospital.id,
              subject: departmentName,
              date: selectedDate,
            },
          });
          setBookedTimes(response.data || []);
        } catch (err) {
          console.error('예약된 시간 정보를 불러오는 데 실패했습니다:', err);
          setError('예약 가능한 시간 정보를 가져오는 데 실패했습니다.');
          setBookedTimes([]);
        } finally {
          setTimeLoading(false);
        }
      };
      fetchBookedTimes();
    } else {
      setBookedTimes([]);
    }
  }, [selectedDate, hospital, departmentName]);

  const timeSlots = useMemo(() => {
    if (!selectedDate) return [];
    const slots = [];
    for (let i = 9; i <= 17; i++) {
      slots.push(`${String(i).padStart(2, '0')}:00`);
      slots.push(`${String(i).padStart(2, '0')}:30`);
    }
    return slots;
  }, [selectedDate]);

  const handleReservationSubmit = async (e) => {
    e.preventDefault();
    if (!selectedDate || !selectedTime) {
      setError('예약 날짜와 시간을 모두 선택해주세요.');
      return;
    }
    setLoading(true);
    try {
      const reservationData = {
        hospitalId: hospital.id,
        subjectName: departmentName,
        reservationTime: `${selectedDate}T${selectedTime}:00`,
      };
      await api.post('/reservations', reservationData);
      alert('예약이 성공적으로 완료되었습니다.');
      navigate('/MyPage');
    } catch (err) {
      setError(err.response?.data?.message || '예약 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (!hospital) return null;

  return (
    <div className="reservation-container">
      <header className="page-header">
        <h1 className="logo" onClick={() => navigate("/")}>닥큐</h1>
        <h2>병원 예약</h2>
      </header>
      
      <main className="reservation-main">
        <div className="hospital-info-card">
          <h3>{hospital.yadmNm}</h3>
          <p>{hospital.addr}</p>
          <p><strong>선택한 진료과:</strong> {departmentName}</p>
        </div>

        <form className="reservation-form" onSubmit={handleReservationSubmit}>
          <h3>예약 정보 입력</h3>
          <div className="form-group">
            <label htmlFor="date-picker">예약 날짜 선택</label>
            <input
              type="date"
              id="date-picker"
              value={selectedDate}
              onChange={(e) => {
                setSelectedDate(e.target.value);
                setSelectedTime('');
              }}
              required
            />
          </div>

          {}
          <div className="form-group">
            <label>예약 시간 선택</label>
            {!selectedDate ? (
              <div className="time-slot-placeholder">날짜를 먼저 선택해주세요.</div>
            ) : timeLoading ? (
              <div className="time-slot-placeholder">예약 가능 시간을 불러오는 중...</div>
            ) : (
              <div className="time-slots-container">
                {timeSlots.map(time => {
                  const isBooked = bookedTimes.includes(time);
                  const isSelected = selectedTime === time;
                  return (
                    <button
                      type="button"
                      key={time}
                      className={`time-slot-btn ${isSelected ? 'selected' : ''}`}
                      onClick={() => !isBooked && setSelectedTime(time)}
                      disabled={isBooked}
                    >
                      {time}
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          {error && <p className="error-message">{error}</p>}

          <button type="submit" className="submit-reservation-btn" disabled={loading || !selectedTime}>
            {loading ? '예약 처리 중...' : '예약 신청'}
          </button>
        </form>
      </main>
    </div>
  );
};

export default Reservation;