// js/topic.js
const topicDisplayArea = document.getElementById('topicDisplayArea');
const voteFormArea = document.getElementById('voteFormArea');
const voteForm = document.getElementById('voteForm');
const resultsArea = document.getElementById('resultsArea');
const optionsContainer = document.getElementById('optionsContainer');

// 오늘의 질문 상태(질문, 투표 여부, 결과)를 로드
async function loadTopicStatus() {
    try {
        const res = await callApi('/api/topic/status', 'GET');
        if (res && res.data) {
            const data = res.data;
            renderTopic(data.topic); // 질문 정보 렌더링

            if (data.myChoice) {
                renderResults(data); // 투표했으면 결과 표시
            } else {
                voteFormArea.style.display = 'block'; // 투표 안 했으면 폼 표시
            }
        }
    } catch (e) {
        topicDisplayArea.innerHTML = '<p>질문을 불러오지 못했습니다.</p>';
    }
}

// 질문 정보와 옵션을 HTML에 렌더링
function renderTopic(topic) {
    document.getElementById('currentTopicId').value = topic.topicId;
    topicDisplayArea.innerHTML = `<h3 style="color:#c0392b;">${topic.question}</h3>`;
    optionsContainer.innerHTML = `
        <label class="vote-option-label" style="background:#e7f3ff;">
            <input type="radio" name="choice" value="A" required> ${topic.optionA}
        </label>
        <label class="vote-option-label" style="background:#fff3e7;">
            <input type="radio" name="choice" value="B" required> ${topic.optionB}
        </label>
    `;
}

// 투표 결과를 HTML에 렌더링하고 폼 숨기기
function renderResults(data) {
    voteFormArea.style.display = 'none';
    resultsArea.style.display = 'block';

    const total = data.totalVotes;
    const countA = data.voteCounts.A || 0;
    const countB = data.voteCounts.B || 0;
    const ratioA = total ? ((countA/total)*100).toFixed(1) : 0;
    const ratioB = total ? ((countB/total)*100).toFixed(1) : 0;

    // 투표율 통계 표시
    document.getElementById('voteStats').innerHTML = `
        <div style="display:flex; height:20px; background:#ddd; border-radius:10px; overflow:hidden;">
            <div style="width:${ratioA}%; background:#3498db;"></div>
            <div style="width:${ratioB}%; background:#e74c3c;"></div>
        </div>
        <div style="display:flex; justify-content:space-between; margin-top:5px;">
            <span style="color:#3498db;">A: ${ratioA}% (${countA}표)</span>
            <span style="color:#e74c3c;">B: ${ratioB}% (${countB}표)</span>
        </div>
    `;

    // 투표 이유 목록 표시
    const list = document.getElementById('reasonsList');
    list.innerHTML = data.reasons.map(r => `
        <div style="border-bottom:1px solid #eee; padding:5px;">
            <strong>${r.memberId}</strong> (${r.choice}): ${r.reason || '-'}
        </div>
    `).join('');
}

// 투표 폼 제출 이벤트 처리
if (voteForm) {
    voteForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            topicId: document.getElementById('currentTopicId').value,
            choice: voteForm.choice.value,
            reason: document.getElementById('voteReason').value
        };
        try {
            const res = await callApi('/api/topic/vote', 'POST', data);
            if (res) renderResults(res.data); // 투표 성공 시 결과 새로고침
        } catch (e) {
            alert(e.message);
        }
    });
}