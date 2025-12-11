// js/readingHistory.js
const addLogModal = document.getElementById('addLogModal');
const addLogForm = document.getElementById('addLogForm');
const closeModalButton = document.getElementById('closeModalButton');
const historyListDiv = document.getElementById('historyList');

// 독서 기록 추가 모달 열기
function openAddLogModal(bookId, bookTitle) {
    if(!addLogModal) return;
    addLogForm.reset();
    document.getElementById('modalBookId').value = bookId;
    document.getElementById('modalBookTitle').textContent = `[${bookTitle}] 기록 추가`;
    document.getElementById('readStartDate').valueAsDate = new Date();
    addLogModal.style.display = 'flex';
}
// 모달 닫기 이벤트 연결
if (closeModalButton) closeModalButton.addEventListener('click', () => addLogModal.style.display = 'none');

// 독서 기록 저장 요청 처리
if (addLogForm) {
    addLogForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            bookId: document.getElementById('modalBookId').value,
            readStatus: document.getElementById('readStatus').value,
            readStartDate: document.getElementById('readStartDate').value,
            memo: document.getElementById('memo').value
        };
        try {
            const res = await callApi('/api/reading-history/add', 'POST', data);
            if (res) {
                alert(res.message);
                addLogModal.style.display = 'none';
            }
        } catch (e) {
            alert(e.message);
        }
    });
}

// 내 독서 기록 목록 로드
async function loadMyReadingHistories() {
    if(!historyListDiv) return;
    try {
        const res = await callApi('/api/reading-history/my', 'GET');
        displayHistoryList(res ? res.data : []);
    } catch (e) {
        historyListDiv.innerHTML = '<p class="no-results">로딩 실패</p>';
    }
}

// 독서 기록 목록을 HTML로 렌더링 (책 제목, 작가, 상태, 삭제 버튼 포함)
function displayHistoryList(histories) {
    historyListDiv.innerHTML = '';

    if (!histories || histories.length === 0) {
        historyListDiv.innerHTML = '<p class="no-results">아직 기록된 독서 기록이 없습니다.</p>';
        return;
    }

    histories.forEach(h => {
        const div = document.createElement('div');
        div.className = 'history-item';

        // 카드 레이아웃 스타일 설정
        div.style.display = 'flex';
        div.style.justifyContent = 'space-between';
        div.style.alignItems = 'flex-start';
        div.style.marginBottom = '15px';
        div.style.padding = '15px';
        div.style.border = '1px solid #eee';
        div.style.borderRadius = '5px';
        div.style.backgroundColor = '#fff';

        // 상태 선택 옵션 HTML 생성
        const options = ['READING', 'COMPLETED', 'STOPPED'].map(opt => {
            const label = opt === 'READING' ? '읽는 중' : (opt === 'COMPLETED' ? '완독' : '중단');
            return `<option value="${opt}" ${h.readStatus === opt ? 'selected' : ''}>${label}</option>`;
        }).join('');

        // 작가 이름 처리
        const authorText = h.bookAuthor ? ` - ${h.bookAuthor}` : '';

        div.innerHTML = `
            <div style="flex-grow: 1; margin-right: 15px;">
                <div class="book-header" style="margin-bottom: 8px;">
                    <span style="font-size: 1.1em; font-weight: bold; color: #2c3e50;">${h.bookTitle}</span>
                    <span style="font-size: 0.9em; color: #7f8c8d; margin-left: 5px;">${authorText}</span>
                </div>
                
                <div style="margin-bottom: 8px;">
                    <select class="status-select" 
                            data-id="${h.readingHistoryId}" 
                            style="padding: 4px; border-radius: 4px; border: 1px solid #ccc; font-size: 0.9em;">
                        ${options}
                    </select>
                </div>
                
                <div style="font-size: 0.85em; color: #666; background-color: #f9f9f9; padding: 8px; border-radius: 4px;">
                    ${h.memo || '메모가 없습니다.'} 
                    <span style="color: #999; margin-left: 5px;">(${h.readStartDate})</span>
                </div>
            </div>

            <div style="flex-shrink: 0;">
                <button class="btn-delete" 
                        data-id="${h.readingHistoryId}" 
                        style="width: auto; padding: 6px 12px; font-size: 0.9em; background-color: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer; white-space: nowrap;">
                    삭제
                </button>
            </div>
        `;
        historyListDiv.appendChild(div);
    });
}

// 상태 변경 및 삭제 이벤트 처리
if (historyListDiv) {
    // 독서 상태 변경 이벤트 처리
    historyListDiv.addEventListener('change', async (e) => {
        if (e.target.classList.contains('status-select')) {
            const id = e.target.dataset.id;
            const status = e.target.value;
            if(confirm('상태를 변경하시겠습니까?')) {
                await callApi(`/api/reading-history/${id}/status`, 'PATCH', { readStatus: status });
                loadMyReadingHistories();
            }
        }
    });

    // 독서 기록 삭제 이벤트 처리
    historyListDiv.addEventListener('click', async (e) => {
        if (e.target.classList.contains('btn-delete')) {
            if(confirm('삭제하시겠습니까?')) {
                await callApi(`/api/reading-history/${e.target.dataset.id}`, 'DELETE');
                loadMyReadingHistories();
            }
        }
    });
}