package com.example.readlog.global;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    // 1. 응답의 성공/실패 여부를 나타냅니다.
    private final boolean success;

    // 2. 사용자에게 보여줄 메시지 (예: "회원가입이 완료되었습니다.")
    private final String message;

    // 3. 실제 반환될 데이터 (GET 요청의 결과, DTO 객체 등). 데이터가 없으면 null.
    private final T data;

    // --- 정적 팩토리 메서드 (성공 응답 생성) ---

    // 1. 데이터가 함께 반환되는 경우 (예: GET 요청)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 2. 메시지만 반환되는 경우 (예: POST/DELETE 성공)
    public static <T> ApiResponse<T> success(String message) {
        // 데이터는 null로 설정
        return new ApiResponse<>(true, message, null);
    }

    // --- 정적 팩토리 메서드 (실패 응답 생성) ---

    // 오류 메시지만 반환되는 경우
    public static ApiResponse<?> error(String message) {
        // 실패이므로 data는 null, success는 false
        return new ApiResponse<>(false, message, null);
    }
}