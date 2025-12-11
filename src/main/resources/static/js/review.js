// js/review.js

// 페이지 로드 시 URL을 확인하고 해당 기능(리뷰 상세 또는 내 리뷰 목록)을 로드
document.addEventListener('DOMContentLoaded', () => {
    const path = window.location.pathname;

    // 리뷰 상세 페이지인 경우
    if (path.includes('review-detail.html')) {
        const params = new URLSearchParams(window.location.search);
        const bookId = params.get('bookId');
        const bookTitle = params.get('bookTitle');

        if (bookId) {
            document.getElementById('targetBookId').value = bookId;
            document.getElementById('pageTitle').textContent = `${bookTitle} 리뷰`;
            loadBookReviews(bookId); // 책별 리뷰 로드
        }
    }
    // 내 리뷰 페이지인 경우
    else if (path.includes('my-reviews.html')) {
        loadMyReviews(); // 내 리뷰 목록 로드
    }
});

// 특정 책의 리뷰 목록을 조회
async function loadBookReviews(bookId) {
    const container = document.getElementById('reviewListContainer');
    try {
        // 비로그인 사용자도 리뷰를 볼 수 있도록 session을 사용하지 않는 API 호출
        const res = await callApi(`/api/reviews?bookId=${bookId}`, 'GET');
        renderReviews(res.data, container, bookId); // bookId를 렌더링에 전달
    } catch (e) {
        container.innerHTML = '<p class="no-results">리뷰 로딩 실패</p>';
    }
}

// 현재 로그인 사용자의 리뷰 목록을 조회
async function loadMyReviews() {
    const container = document.getElementById('myReviewsList');
    try {
        const res = await callApi('/api/reviews/my', 'GET');
        // 내 리뷰 페이지에서는 bookId가 필요 없으므로 null 전달
        renderReviews(res.data, container, null, true);
    } catch (e) {
        container.innerHTML = '<p class="no-results">로딩 실패</p>';
    }
}

// 리뷰 작성 폼 제출 이벤트 처리
const createForm = document.getElementById('createReviewForm');
if (createForm) {
    createForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const bookId = document.getElementById('targetBookId').value;
        const data = {
            bookId: bookId,
            rating: document.getElementById('reviewRating').value,
            content: document.getElementById('reviewContent').value
        };
        try {
            await callApi('/api/reviews', 'POST', data);
            alert('리뷰 등록 완료');
            createForm.reset();
            loadBookReviews(bookId); // 리뷰 목록 새로고침
        } catch (e) {
            alert(e.message);
        }
    });
}

// 리뷰 목록을 HTML로 렌더링 (답글 및 좋아요 포함)
function renderReviews(reviews, container, currentBookId, isMyReview = false) {
    container.innerHTML = '';
    if (!reviews || !reviews.length) {
        container.innerHTML = '<p class="no-results">리뷰가 없습니다.</p>';
        return;
    }
    reviews.forEach(r => {
        // 1. 답글 목록 HTML 생성
        const repliesHtml = r.replies && r.replies.length > 0
            ? '<div class="replies-container">' + r.replies.map(reply => `
                <div class="reply-item" style="display:flex; justify-content:space-between; align-items:center; padding: 5px 0; border-bottom: 1px dotted #eee;">
                    <div style="flex-grow: 1; font-size: 0.9em;">
                        <strong>${reply.memberId}</strong>: ${reply.content}
                    </div>
                    
                    <div style="display:flex; align-items:center; gap: 8px; flex-shrink: 0;">
                        <span style="font-size: 0.75em; color: #888;"> (${new Date(reply.createdAt).toLocaleDateString()})</span>
                        ${reply.isMine ? `
                            <button class="btn-delete-reply" data-id="${reply.replyId}"
                                    style="padding: 2px 6px; font-size: 0.7em; background:#f0ad4e; color:white; border:none; border-radius:3px;">
                                삭제
                            </button>` : ''}
                    </div>
                </div>
            `).join('') + '</div>'
            : '';

        // 2. 리뷰 아이템 HTML 구조
        const div = document.createElement('div');
        div.className = 'review-item';
        div.setAttribute('data-review-id', r.reviewId); // 이벤트 위임을 위해 ID 추가

        div.innerHTML = `
            <div style="margin-bottom: 10px;">
                <strong>${isMyReview ? r.bookTitle : r.memberId}</strong> 
                <span style="color:#f39c12;">${'⭐'.repeat(r.rating)}</span>
                <p>${r.content}</p>
                
                <div class="review-meta" style="font-size: 0.9em; color: #555; margin-top: 5px; display:flex; align-items:center;">
                    <button class="btn-like" data-id="${r.reviewId}" 
                            style="width:auto; padding:5px; margin-right: 15px; background: none; color: ${r.isLiked ? '#e74c3c' : '#888'};">
                        ${r.isLiked ? '❤️' : '🤍'} 좋아요 (${r.likeCount || 0}) 
                    </button>
                    <span>답글 ${r.replies ? r.replies.length : 0}개</span>
                </div>
            </div>
            
            <div class="reply-section">
                ${repliesHtml}
                <div class="reply-input-area" style="margin-top: 10px; display:flex; gap:5px;">
                    <input type="text" id="replyContent-${r.reviewId}" placeholder="답글 작성" style="flex-grow: 1; padding: 5px; border: 1px solid #ccc;">
                    <button class="btn-create-reply" data-id="${r.reviewId}" style="width:auto; padding:5px 10px; background:#2ecc71; color:white;">등록</button>
                </div>
            </div>

            ${isMyReview || r.isMine ? `<button class="btn-delete-review" data-id="${r.reviewId}" style="width:auto; background:#e74c3c; color:white; padding:5px;">리뷰 삭제</button>` : ''}
        `;
        container.appendChild(div);
    });

    // ⭐ 새로고침 대신 이벤트 위임을 통해 버튼에 이벤트 리스너를 한 번만 등록
    if (container.getAttribute('data-events-attached') !== 'true') {
        attachEventListeners(container, currentBookId);
        container.setAttribute('data-events-attached', 'true');
    }
}


// --- 4. 이벤트 핸들러 함수 정의 ---

function attachEventListeners(container, currentBookId) {
    container.addEventListener('click', (e) => {
        const target = e.target;
        const reviewId = target.dataset.id || target.closest('.review-item')?.dataset.reviewId;

        if (target.classList.contains('btn-like')) {
            toggleLike(reviewId, currentBookId);
        } else if (target.classList.contains('btn-create-reply')) {
            createReply(reviewId, currentBookId);
        } else if (target.classList.contains('btn-delete-reply')) {
            deleteReply(target.dataset.id, currentBookId);
        } else if (target.classList.contains('btn-delete-review')) {
            deleteReview(reviewId);
        }
    });
}

// 리뷰 삭제 요청 처리 후 페이지 새로고침 (기존 함수 유지)
async function deleteReview(id) {
    if(confirm('리뷰를 삭제하시겠습니까?')) {
        await callApi(`/api/reviews/${id}`, 'DELETE');
        window.location.reload(); // 단순화를 위해 페이지 전체 새로고침
    }
}

// 좋아요/취소 토글 및 목록 새로고침
async function toggleLike(reviewId, currentBookId) {
    try {
        const res = await callApi(`/api/reviews/${reviewId}/like`, 'POST');
        alert(res.message);
        // 좋아요만 업데이트되었으므로 해당 책의 리뷰 목록만 새로고침
        if (currentBookId) loadBookReviews(currentBookId);
        else window.location.reload();
    } catch(e) {
        alert(e.message);
    }
}

// 답글 생성 및 목록 새로고침
async function createReply(reviewId, currentBookId) {
    const input = document.getElementById(`replyContent-${reviewId}`);
    const content = input.value;
    if (!content.trim()) return alert('답글 내용을 입력해주세요.');

    try {
        await callApi(`/api/reviews/${reviewId}/replies`, 'POST', { content: content });
        alert('답글 등록 완료');
        input.value = ''; // 입력창 비우기
        // 답글만 업데이트되었으므로 해당 책의 리뷰 목록만 새로고침
        if (currentBookId) loadBookReviews(currentBookId);
        else window.location.reload();
    } catch(e) {
        alert(e.message);
    }
}

// 답글 삭제 및 목록 새로고침
async function deleteReply(replyId, currentBookId) {
    if(confirm('답글을 삭제하시겠습니까?')) {
        try {
            await callApi(`/api/reviews/replies/${replyId}`, 'DELETE');
            alert('답글 삭제 완료');
            // 답글만 업데이트되었으므로 해당 책의 리뷰 목록만 새로고침
            if (currentBookId) loadBookReviews(currentBookId);
            else window.location.reload();
        } catch(e) {
            alert(e.message);
        }
    }
}