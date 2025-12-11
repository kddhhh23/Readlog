// js/auth.js
const registerForm = document.getElementById('registerForm');
const loginForm = document.getElementById('loginForm');

// 회원가입 요청 처리
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            memberId: document.getElementById('regId').value,
            password: document.getElementById('regPw').value,
            name: document.getElementById('regName').value,
            email: document.getElementById('regEmail').value,
            age: document.getElementById('regAge').value ? parseInt(document.getElementById('regAge').value) : null,
            phoneNumber: document.getElementById('regPhone').value,
            school: document.getElementById('regSchool').value
        };

        try {
            const res = await callApi('/api/member/register', 'POST', data);
            if (res) {
                alert(`✅ 회원가입 성공: ${res.message}`);
                registerForm.reset();
            }
        } catch (error) {
            alert(`❌ 회원가입 실패: ${error.message}`);
        }
    });
}

// 로그인 요청 처리
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const memberId = document.getElementById('loginId').value;
        const data = {
            memberId: memberId,
            password: document.getElementById('loginPw').value
        };

        try {
            const res = await callApi('/api/member/login', 'POST', data);
            if (res) {
                alert(`✅ 로그인 성공: ${res.message}`);
                sessionStorage.setItem('memberId', memberId);
                window.location.href = '/search.html'; // 페이지 이동
            }
        } catch (error) {
            alert(`❌ 로그인 실패: ${error.message}`);
        }
    });
}