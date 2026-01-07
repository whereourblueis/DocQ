/*
MyPage.jsx
로그인한 사용자의 개인화된 정보를 보여주는 마이페이지입니다.
사용자의 예약 내역(예정/완료)과 작성한 리뷰 목록을 보여줍니다. 또한 진료가 완료된 예약 건에 대해 리뷰를 작성할 수 있습니다.

/api/reservations/my API를 호출하여 내 예약 목록 조회
예약 상태(예약 확정, 진료 완료 등)에 따라 다른 UI 표시
리뷰 작성이 필요한 경우 ReviewModal 컴포넌트를 띄워주는 기능
 */


import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import ReviewModal from '../components/ReviewModal';
import '../components/MyPage.css';

// --- 헬퍼 함수 (수정 없음) ---
const getStatusInfo = (status, reservationTime) => {
  const isPast = new Date(reservationTime) < new Date();
  if (status === 'RESERVED' && isPast) {
    return { text: '진료 완료', style: 'status-completed' };
  }
  switch (status) {
    case 'RESERVED': return { text: '예약 확정', style: 'status-reserved' };
    case 'COMPLETED': return { text: '진료 완료', style: 'status-completed' };
    case 'REVIEWED': return { text: '리뷰 완료', style: 'status-reviewed' };
    case 'CANCELED': return { text: '예약 취소', style: 'status-canceled' };
    default: return { text: status, style: 'status-reviewed' };
  }
};

const MyPage = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [reservations, setReservations] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reviewingReservation, setReviewingReservation] = useState(null);
  const [activeTab, setActiveTab] = useState('upcoming');

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [userResponse, reservationsResponse, reviewsResponse] = await Promise.all([
        api.get('/user/info'),
        api.get('/reservations/my'),
        api.get('/reviews/my'),
      ]);

      const sortedReservations = (reservationsResponse.data || []).sort(
        (a, b) => new Date(b.reservationTime) - new Date(a.reservationTime)
      );

      setUser(userResponse.data.user);
      setReservations(sortedReservations);
      setReviews(reviewsResponse.data || []);
    } catch (err) {
      if (err.response?.status !== 401) {
        setError('마이페이지 정보를 불러오는 데 실패했습니다.');
        console.error(err);
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const filteredReservations = reservations.filter(res => {
    const isPast = new Date(res.reservationTime) < new Date();
    if (activeTab === 'upcoming') return !isPast && res.status !== 'CANCELED';
    if (activeTab === 'past') return isPast || res.status === 'CANCELED';
    return false;
  });

  const handleReviewSuccess = () => {
    setReviewingReservation(null);
    fetchData();
  };
  
  if (loading) return <div className="mypage-message-container"><h2>정보를 불러오는 중...</h2></div>;
  if (error) return <div className="mypage-message-container error-message"><h2>{error}</h2></div>;

  return (
    <div className="mypage-container">
      <header className="mypage-header">
        <h1 className="logo" onClick={() => navigate('/')}>닥큐</h1>
        <h2>마이페이지</h2>
      </header>

      <main className="mypage-content">
        {user && (
          <section className="user-profile-card">
            <h3>💁‍♂️ 회원 정보</h3>
            <div className="user-info-grid">
              <div className="user-info-item"><strong>이름</strong><span>{user.name}</span></div>
              <div className="user-info-item"><strong>이메일</strong><span>{user.email}</span></div>
            </div>
          </section>
        )}

        <section className="reservations-section">
          <div className="tabs-container">
            <button className={`tab-item ${activeTab === 'upcoming' ? 'active' : ''}`} onClick={() => setActiveTab('upcoming')}>예정된 예약</button>
            <button className={`tab-item ${activeTab === 'past' ? 'active' : ''}`} onClick={() => setActiveTab('past')}>지난 내역</button>
            <button className={`tab-item ${activeTab === 'reviews' ? 'active' : ''}`} onClick={() => setActiveTab('reviews')}>작성한 리뷰</button>
          </div>
          
          <div className="tab-content">
            {activeTab !== 'reviews' && (
              <div className="reservation-list">
                {filteredReservations.length > 0 ? (
                  filteredReservations.map(res => <ReservationCard key={res.reservationId} reservation={res} onReviewClick={() => setReviewingReservation(res)} />)
                ) : (
                  <div className="no-reservations-placeholder">
                    <h4>{activeTab === 'upcoming' ? '예정된 예약이 없습니다.' : '지난 예약 내역이 없습니다.'}</h4>
                  </div>
                )}
              </div>
            )}

            {activeTab === 'reviews' && (
              <div className="review-list">
                {reviews.length > 0 ? (
                  reviews.map(review => <ReviewCard key={review.id} review={review} />)
                ) : (
                  <div className="no-reservations-placeholder">
                    <h4>작성한 리뷰가 없습니다.</h4>
                    <p>진료 완료 후 리뷰를 남겨보세요!</p>
                  </div>
                )}
              </div>
            )}
          </div>
        </section>
      </main>

      {reviewingReservation && (
        <ReviewModal reservation={reviewingReservation} onClose={() => setReviewingReservation(null)} onSubmitSuccess={handleReviewSuccess} />
      )}
    </div>
  );
};

const ReservationCard = ({ reservation, onReviewClick }) => {
  const { text: statusText, style: statusStyle } = getStatusInfo(reservation.status, reservation.reservationTime);
  const isReviewable = statusText === '진료 완료';
  const isReviewed = reservation.status === 'REVIEWED';

  return (
    <div className="reservation-card">
      <div className="card-info">
        <h4>{reservation.hospitalName}</h4>
        <div className={`status-badge ${statusStyle}`}>{statusText}</div>
        <div className="card-details">
          <p>🩺 <strong>진료과:</strong> {reservation.subjectName}</p>
          <p>🗓️ <strong>예약일시:</strong> {new Date(reservation.reservationTime).toLocaleString('ko-KR')}</p>
        </div>
      </div>
      <div className="card-actions">
        {isReviewable && <button className="review-btn" onClick={onReviewClick}>리뷰 작성</button>}
        {isReviewed && <div className="review-done-badge">작성 완료</div>}
      </div>
    </div>
  );
};

// --- 💡 [추가] 태그 영문명을 한글로 바꾸기 위한 객체 ---
const TAG_KOREAN = {
  KIND: '친절해요',
  CLEAN: '청결해요',
  COMFORTABLE: '진료 분위기가 편안해요',
  QUICK: '진료가 빨라요'
};

const ReviewCard = ({ review }) => {
  const renderStars = (rating) => {
    const filledStar = '★';
    const emptyStar = '☆';
    return filledStar.repeat(rating) + emptyStar.repeat(5 - rating);
  };

  return (
    <div className="review-item-card">
      <h4 className="review-hospital-name">{review.hospitalName}</h4>

      <div className="review-card-body">
        {review.comment ? (
          // 1. 별점 + 텍스트 리뷰
          <div className="text-review-content">
            <div className="review-rating">{renderStars(review.rating)}</div>
            <p className="review-comment">{review.comment}</p>
          </div>
        ) : (
          // 2. 태그 리뷰
          <div className="review-tags-container">
            {(review.tags || []).map(tag => (
              <span key={tag} className="review-tag">
                {TAG_KOREAN[tag] || tag}
              </span>
            ))}
          </div>
        )}
      </div>

      <div className="review-timestamp">
        {review.createdAt ? new Date(review.createdAt).toLocaleDateString('ko-KR') : ''}
      </div>
    </div>
  );
};

export default MyPage;