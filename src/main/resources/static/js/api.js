// js/api.js

// API 호출 및 응답 처리
async function callApi(url, method, data = null) {
    const options = {
        method: method,
        headers: { 'Content-Type': 'application/json' }
    };
    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(url, options);

        const contentType = response.headers.get('content-type');
        const contentLength = response.headers.get('content-length');
        const isJson = contentType && contentType.includes('application/json');

        let apiResponse = null;

        // JSON 응답 파싱
        if (isJson && (contentLength === null || parseInt(contentLength) > 0)) {
            apiResponse = await response.json();
        } else {
            apiResponse = {
                success: response.ok,
                message: response.ok ? "요청 성공" : `요청 실패 (HTTP ${response.status})`
            };
        }

        // 성공 응답 반환
        if (response.ok && (apiResponse.success !== false)) {
            return apiResponse;
        }

        // 에러 객체 생성 및 throw
        const errorObj = new Error(apiResponse.message || `요청 실패 (HTTP ${response.status})`);
        errorObj.status = response.status;
        errorObj.api = apiResponse;
        throw errorObj;

    } catch (error) {
        if (!(error instanceof Error)) {
            error = new Error("알 수 없는 오류 발생");
        }
        if (!error.status) error.status = 0;

        throw error;
    }
}