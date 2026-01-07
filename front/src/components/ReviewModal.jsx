/*
ReviewModal.jsx
진료 후 리뷰를 작성하는 팝업 창 컴포넌트입니다.
MyPage에서 '리뷰 작성' 버튼을 누르면 나타납니다. 별점, 태그, 텍스트 코멘트 등 다양한 형태로 리뷰를 작성할 수 있습니다.
 */

import React, { useState } from 'react';
import api from '../api/axios';
import './ReviewModal.css';

const TAG_OPTIONS = ['KIND', 'CLEAN', 'COMFORTABLE', 'QUICK'];
const TAG_KOREAN = {
  KIND: '친절해요',
  CLEAN: '청결해요',
  COMFORTABLE: '진료 분위기가 편안해요',
  QUICK: '진료가 빨라요'
};

const StarIcon = ({ filled, onClick, onMouseEnter, onMouseLeave }) => (
  <svg
    onClick={onClick}
    onMouseEnter={onMouseEnter}
    onMouseLeave={onMouseLeave}
    className={`star-icon ${filled ? 'filled' : ''}`}
    viewBox="0 0 24 24"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
  </svg>
);

const ReviewModal = ({ reservation, onClose, onSubmitSuccess }) => {
  const [reviewType, setReviewType] = useState('text');
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [contents, setContents] = useState('');
  const [selectedTags, setSelectedTags] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleTagClick = (tag) => {
    setSelectedTags(prev =>
      prev.includes(tag) ? prev.filter(t => t !== tag) : [...prev, tag]
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (reviewType === 'text') {
      if (rating === 0) {
        setError('별점을 선택해주세요.');
        return;
      }
      if (contents.length < 10) {
        setError('텍스트 리뷰는 10자 이상 작성해주세요.');
        return;
      }
    } else {
      if (selectedTags.length === 0) {
        setError('태그를 하나 이상 선택해주세요.');
        return;
      }
    }
    
    setLoading(true);
    try {
      let payload = { reservationId: reservation.reservationId };
      let url = '';

      if (reviewType === 'text') {
        url = '/reviews/text';
        // --- 💡 바로 이 부분입니다! ---
        // 서버가 기대하는 'comment'라는 이름으로 보내도록 수정합니다.
        payload = { ...payload, rating, comment: contents };
      } else {
        url = '/reviews/tag';
        payload = { ...payload, tags: selectedTags };
      }

      await api.post(url, payload);
      alert('리뷰가 성공적으로 등록되었습니다.');
      onSubmitSuccess();
      onClose();
      
    } catch (err) {
      setError(err.response?.data?.message || '리뷰 등록에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="review-modal-backdrop" onClick={onClose}>
      <div className="review-modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="close-modal-btn" onClick={onClose}>&times;</button>
        <h2>{reservation.hospitalName} 리뷰 작성</h2>
        
        <div className="review-type-tabs">
          <button className={reviewType === 'text' ? 'active' : ''} onClick={() => setReviewType('text')}>
            ✍️ 텍스트 리뷰
          </button>
          <button className={reviewType === 'tag' ? 'active' : ''} onClick={() => setReviewType('tag')}>
            👍 간편 태그 리뷰
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {reviewType === 'text' ? (
            <>
              <div className="form-group">
                <label>별점</label>
                <div
                  className="star-rating-container"
                  onMouseLeave={() => setHoverRating(0)}
                >
                  {[1, 2, 3, 4, 5].map((star) => (
                    <StarIcon
                      key={star}
                      filled={star <= (hoverRating || rating)}
                      onClick={() => setRating(star)}
                      onMouseEnter={() => setHoverRating(star)}
                    />
                  ))}
                </div>
              </div>

              <div className="form-group">
                <label>리뷰 내용</label>
                <textarea
                  className="review-textarea"
                  value={contents}
                  onChange={(e) => setContents(e.target.value)}
                  placeholder="진료 후기를 10자 이상 남겨주세요."
                />
              </div>
            </>
          ) : (
            <div className="form-group">
              <label>어떤 점이 좋았나요? (중복 선택 가능)</label>
              <div className="tag-container">
                {TAG_OPTIONS.map(tag => (
                  <button
                    type="button"
                    key={tag}
                    className={`tag-btn ${selectedTags.includes(tag) ? 'selected' : ''}`}
                    onClick={() => handleTagClick(tag)}
                  >
                    {TAG_KOREAN[tag]}
                  </button>
                ))}
              </div>
            </div>
          )}

          <div className="modal-error">{error}</div>

          <button type="submit" className="submit-review-btn" disabled={loading}>
            {loading ? '등록 중...' : '리뷰 등록하기'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ReviewModal;