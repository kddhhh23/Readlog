package com.example.readlog.global.exception;

import com.example.readlog.global.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request (잘못된 인자 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }

    // 401 Unauthorized 또는 400 Bad Request (상태 오류)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalState(IllegalStateException e) {
        // 기존 코드에서 '로그인' 관련 메시지일 때만 401로 처리하던 로직을 유지
        if (e.getMessage().contains("로그인")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }

    // 403 Forbidden (권한 없음)
    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalAccess(IllegalAccessError e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
    }

    // 500 Internal Server Error (그 외 모든 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        e.printStackTrace(); // 서버 로그에 에러 출력
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
    }
}