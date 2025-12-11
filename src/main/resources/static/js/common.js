// js/common.js

// 페이지 로드 시 세션 확인 및 헤더 렌더링
document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    const isLoginPage = currentPath.endsWith('index.html') || currentPath === '/';
    const memberId = sessionStorage.getItem('memberId');

    // 비로그인 시 로그인 페이지로 리다이렉션
    if (!isLoginPage && !memberId) {
        alert('로그인이 필요합니다.');
        window.location.href = '/index.html';
        return;
    }

    // 로그인 상태일 때 공통 헤더를 렌더링
    const headerArea = document.getElementById('common-header');
    if (headerArea && memberId) {
        headerArea.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 2px solid #eee;">
                <h1 style="cursor: pointer; margin:0;" onclick="location.href='/search.html'">ReadLog</h1>
                <div style="display: flex; gap: 10px;">
                    <span style="display:flex; align-items:center; font-weight:bold; margin-right:10px; color: #555;">👤 ${memberId}님</span>
                    <button onclick="location.href='/search.html'" style="width:auto; margin:0; padding: 8px 12px; background-color:#2ecc71; color:white;">책 검색</button>
                    <button onclick="location.href='/topic.html'" style="width:auto; margin:0; padding: 8px 12px; background-color:#f39c12; color:white;">오늘의 질문</button>
                    <button onclick="location.href='/social.html'" style="width:auto; margin:0; padding: 8px 12px; background-color:#3498db; color:white;">소셜</button>
                    <button onclick="handleLogout()" style="width:auto; margin:0; padding: 8px 12px; background-color:#f0ad4e; color:white;">로그아웃</button>
                </div>
            </div>
        `;
    }

    // 전역 변수 설정 (기존 코드와의 호환성 유지)
    window.currentUserId = memberId;
});

// 로그아웃 처리 및 페이지 이동
async function handleLogout() {
    try {
        await callApi('/api/member/logout', 'POST');
    } catch (e) {
        console.error("로그아웃 API 오류:", e);
    } finally {
        sessionStorage.removeItem('memberId');
        alert('로그아웃 되었습니다.');
        window.location.href = '/index.html';
    }
}