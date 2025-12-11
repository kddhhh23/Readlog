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
        const res = await callApi(`/api/reviews?bookId=${bookId}`, 'GET');
        renderReviews(res.data, container);
    } catch (e) {
        container.innerHTML = '<p class="no-results">리뷰 로딩 실패</p>';
    }
}

// 현재 로그인 사용자의 리뷰 목록을 조회
async function loadMyReviews() {
    const container = document.getElementById('myReviewsList');
    try {
        const res = await callApi('/api/reviews/my', 'GET');
        renderReviews(res.data, container, true);
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

// 리뷰 목록을 HTML로 렌더링
function renderReviews(reviews, container, isMyReview = false) {
    container.innerHTML = '';
    if (!reviews || !reviews.length) {
        container.innerHTML = '<p class="no-results">리뷰가 없습니다.</p>';
        return;
    }
    reviews.forEach(r => {
        const div = document.createElement('div');
        div.className = 'review-item';
        div.innerHTML = `
            <div>
                <strong>${isMyReview ? r.bookTitle : r.memberId}</strong> 
                <span style="color:#f39c12;">${'⭐'.repeat(r.rating)}</span>
                <p>${r.content}</p>
            </div>
            ${isMyReview || r.isMine ? `<button onclick="deleteReview(${r.reviewId})" style="width:auto; background:#e74c3c; color:white; padding:5px;">삭제</button>` : ''}
        `;
        container.appendChild(div);
    });
}

// 리뷰 삭제 요청 처리 후 페이지 새로고침
async function deleteReview(id) {
    if(confirm('삭제하시겠습니까?')) {
        await callApi(`/api/reviews/${id}`, 'DELETE');
        window.location.reload();
    }
}