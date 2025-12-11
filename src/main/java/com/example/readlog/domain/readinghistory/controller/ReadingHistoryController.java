package com.example.readlog.domain.readinghistory.controller;

import com.example.readlog.domain.readinghistory.dto.ReadingHistoryRequestDto;
import com.example.readlog.domain.readinghistory.dto.ReadingHistoryResponseDto;
import com.example.readlog.domain.readinghistory.dto.ReadingHistoryStatusUpdateDto;
import com.example.readlog.domain.readinghistory.service.ReadingHistoryService;
import com.example.readlog.global.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 독서 기록 관련 API를 처리하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reading-history")
public class ReadingHistoryController {

    private final ReadingHistoryService historyService;
    private static final String MEMBER_ID_SESSION_KEY = "LOGGED_IN_MEMBER_ID";

    // 세션에서 현재 로그인된 사용자 ID를 가져옴
    private String getMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute(MEMBER_ID_SESSION_KEY);
        if (memberId == null) {
            throw new IllegalStateException("로그인된 사용자만 이용할 수 있습니다.");
        }
        return memberId;
    }

    // 새로운 독서 기록을 추가
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> createHistory(@RequestBody ReadingHistoryRequestDto dto, HttpSession session) {
        String memberId = getMemberId(session);
        ReadingHistoryResponseDto response = historyService.createHistory(memberId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("독서 기록이 성공적으로 추가되었습니다.", response)
        );
    }

    // 내 독서 기록 목록을 조회
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyHistories(HttpSession session) {
        String memberId = getMemberId(session);
        List<ReadingHistoryResponseDto> response = historyService.getHistoriesByMember(memberId);

        String message = response.isEmpty() ? "기록된 독서 기록이 없습니다." : "총 " + response.size() + "건의 독서 기록을 조회했습니다.";

        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    // 팔로잉하는 사용자들의 독서 기록을 조회
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<?>> getFollowingReadingHistory(HttpSession session) {
        String memberId = getMemberId(session);
        List<ReadingHistoryResponseDto> histories = historyService.getFollowingMembersHistory(memberId);

        String message = histories.isEmpty()
                ? "팔로잉하는 사용자들의 독서 기록이 없습니다."
                : "팔로잉하는 사용자들의 독서 기록 " + histories.size() + "건을 조회했습니다.";

        return ResponseEntity.ok(ApiResponse.success(message, histories));
    }

    // 특정 독서 기록을 삭제
    @DeleteMapping("/{historyId}")
    public ResponseEntity<ApiResponse<?>> deleteHistory(@PathVariable Long historyId, HttpSession session) {
        String memberId = getMemberId(session);
        historyService.deleteHistory(historyId, memberId);

        return ResponseEntity.ok(ApiResponse.success("독서 기록이 성공적으로 삭제되었습니다."));
    }

    // 특정 독서 기록의 상태를 수정
    @PatchMapping("/{historyId}/status")
    public ResponseEntity<ApiResponse<?>> updateHistoryStatus(
            @PathVariable Long historyId,
            @RequestBody ReadingHistoryStatusUpdateDto dto,
            HttpSession session
    ) {
        String memberId = getMemberId(session);
        historyService.updateReadingHistoryStatus(historyId, memberId, dto);

        return ResponseEntity.ok(ApiResponse.success("독서 상태가 성공적으로 변경되었습니다."));
    }
}