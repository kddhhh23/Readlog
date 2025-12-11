// js/follow.js
const socialList = document.getElementById('socialList');
const feedList = document.getElementById('feedList');

// 탭 전환 및 해당 탭의 내용 표시
function switchTab(tab) {
    document.querySelectorAll('.tab-content').forEach(el => el.style.display = 'none');
    document.getElementById('followingActions').style.display = 'none';
    socialList.innerHTML = '';

    if(tab === 'search') {
        document.getElementById('tab-search').style.display = 'block';
    } else if(tab === 'school') {
        loadUserList('/api/member/school');
    } else if(tab === 'following') {
        document.getElementById('followingActions').style.display = 'block';
        loadUserList(`/api/follow/following/${sessionStorage.getItem('memberId')}`, true);
    } else if(tab === 'follower') {
        loadUserList(`/api/follow/follower/${sessionStorage.getItem('memberId')}`);
    }
}

// 회원 검색 폼 제출 이벤트 처리
document.getElementById('socialSearchForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const keyword = document.getElementById('socialSearchKeyword').value;
    loadUserList(`/api/member/search?keyword=${keyword}`);
});

// API를 통해 회원 목록을 로드하고 HTML로 렌더링
async function loadUserList(url, isFollowingTab = false) {
    socialList.innerHTML = '로딩 중...';
    try {
        const res = await callApi(url, 'GET');
        const members = res.data || [];
        // 팔로잉 상태 확인을 위해 내 팔로잉 목록 조회
        const myFollowingRes = await callApi(`/api/follow/following/${sessionStorage.getItem('memberId')}`, 'GET');
        const myFollowingIds = myFollowingRes.data || [];

        socialList.innerHTML = members.map(id => {
            const isFollow = myFollowingIds.includes(id);
            if(id === sessionStorage.getItem('memberId')) return ''; // 나 자신 제외
            return `
                <div style="display:flex; justify-content:space-between; padding:10px; border-bottom:1px solid #eee;">
                    <span>👤 ${id}</span>
                    <button onclick="toggleFollow('${id}')" style="width:auto; margin:0; padding:5px 10px; background:${isFollow?'#e74c3c':'#2ecc71'}; color:white;">
                        ${isFollow ? '언팔로우' : '팔로우'}
                    </button>
                </div>
            `;
        }).join('') || '<p class="no-results">목록이 없습니다.</p>';

    } catch (e) {
        socialList.innerHTML = '<p class="no-results">로딩 실패</p>';
    }
}

// 팔로우/언팔로우 상태를 토글하고 목록을 새로고침
async function toggleFollow(targetId) {
    try {
        const res = await callApi(`/api/follow/${targetId}`, 'POST');
        alert(res.message);
        // 현재 탭을 확인하여 목록을 새로고침
        if(document.getElementById('followingActions').style.display === 'block') switchTab('following');
        else {
            // 탭이 'search' 또는 'school' 일 경우 간단하게 새로고침
            const currentUrl = new URL(window.location.href);
            const keyword = currentUrl.searchParams.get('keyword');
            if (keyword) loadUserList(`/api/member/search?keyword=${keyword}`);
            else loadUserList('/api/member/school');
        }
    } catch (e) {
        alert(e.message);
    }
}

// 팔로잉하는 회원들의 리뷰 피드 로드
async function loadFollowingReviews() {
    feedList.innerHTML = '로딩 중...';
    try {
        const res = await callApi('/api/reviews/following', 'GET');
        feedList.innerHTML = res.data.map(r => `
            <div class="review-item">
                <strong>${r.memberId}</strong>님이 <strong>${r.bookTitle}</strong>에 남긴 리뷰<br>
                ⭐ ${r.rating}: ${r.content}
            </div>
        `).join('') || '리뷰가 없습니다.';
    } catch(e) { feedList.innerHTML = '로딩 실패'; }
}

// 팔로잉하는 회원들의 독서 기록 피드 로드
async function loadFollowingHistory() {
    feedList.innerHTML = '로딩 중...';
    try {
        const res = await callApi('/api/reading-history/following', 'GET');
        feedList.innerHTML = res.data.map(h => `
            <div class="review-item">
                <strong>${h.memberId}</strong>님이 <strong>${h.bookTitle}</strong> 읽는 중 (${h.readStatus})
            </div>
        `).join('') || '기록이 없습니다.';
    } catch(e) { feedList.innerHTML = '로딩 실패'; }
}