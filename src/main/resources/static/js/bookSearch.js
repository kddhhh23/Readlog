// js/bookSearch.js
const searchForm = document.getElementById('searchForm');
const searchResultsDiv = document.getElementById('searchResults');

// 책 검색 폼 제출 이벤트 처리
if (searchForm) {
    searchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const type = document.getElementById('searchType').value;
        const keyword = document.getElementById('searchKeyword').value;
        const url = `/api/books/search?type=${type}&keyword=${encodeURIComponent(keyword)}`;

        searchResultsDiv.innerHTML = '<p class="no-results">검색 중...</p>';
        try {
            const res = await callApi(url, 'GET');
            if (res && res.data) displaySearchResults(res.data);
            else displaySearchResults([]);
        } catch (e) {
            displaySearchResults([]);
        }
    });
}

// 검색 결과를 HTML로 렌더링
function displaySearchResults(books) {
    searchResultsDiv.innerHTML = '';
    if (!books || books.length === 0) {
        searchResultsDiv.innerHTML = '<p class="no-results">검색 결과가 없습니다.</p>';
        return;
    }
    books.forEach(book => {
        const item = document.createElement('div');
        item.className = 'book-result-item';
        item.innerHTML = `
            <div class="book-info-group">
                <div class="book-title">${book.title}</div>
                <div class="book-info">저자: ${book.author} | 출판사: ${book.publisher}</div>
            </div>
            <div class="book-actions">
                <button class="action-button btn-review" data-id="${book.bookId}" data-title="${book.title}">리뷰 보기</button>
                <button class="action-button btn-add-log" data-id="${book.bookId}" data-title="${book.title}">기록 추가</button>
            </div>
        `;
        searchResultsDiv.appendChild(item);
    });
}

// 검색 결과 목록에서 버튼 클릭 이벤트 처리
if (searchResultsDiv) {
    searchResultsDiv.addEventListener('click', (e) => {
        const btn = e.target.closest('.action-button');
        if (!btn) return;

        const bookId = btn.dataset.id;
        const bookTitle = btn.dataset.title;

        if (btn.classList.contains('btn-review')) {
            // 리뷰 상세 페이지로 이동
            window.location.href = `/review-detail.html?bookId=${bookId}&bookTitle=${encodeURIComponent(bookTitle)}`;
        } else if (btn.classList.contains('btn-add-log')) {
            // 독서 기록 모달 열기 함수 호출
            if (typeof openAddLogModal === 'function') openAddLogModal(bookId, bookTitle);
        }
    });
}