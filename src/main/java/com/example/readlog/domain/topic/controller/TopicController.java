package com.example.readlog.domain.topic.controller;

import com.example.readlog.domain.topic.dto.VoteRequestDto;
import com.example.readlog.domain.topic.dto.VoteStatusDto;
import com.example.readlog.domain.topic.service.TopicService;
import com.example.readlog.global.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 오늘의 질문 및 투표 관련 API를 처리하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topic")
public class TopicController {

    private final TopicService topicService;
    private static final String MEMBER_ID_SESSION_KEY = "LOGGED_IN_MEMBER_ID";

    // 세션에서 로그인된 사용자 ID를 가져옴 (없으면 예외 발생)
    private String getMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute(MEMBER_ID_SESSION_KEY);
        if (memberId == null) {
            throw new IllegalStateException("로그인된 사용자만 이용할 수 있습니다.");
        }
        return memberId;
    }

    // 오늘의 질문 상태 및 사용자의 투표 여부를 조회
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getTopicStatus(HttpSession session) {
        String memberId;
        try {
            memberId = getMemberId(session);
        } catch (IllegalStateException e) {
            // 비로그인 사용자도 질문을 볼 수 있도록 "anonymous" 처리
            memberId = "anonymous";
        }

        VoteStatusDto status = topicService.getTodayTopicStatus(memberId);
        return ResponseEntity.ok(
                ApiResponse.success("오늘의 질문 상태를 성공적으로 조회했습니다.", status)
        );
    }

    // 오늘의 질문에 투표를 처리
    @PostMapping("/vote")
    public ResponseEntity<ApiResponse<?>> voteForTopic(@RequestBody VoteRequestDto dto, HttpSession session) {
        String memberId = getMemberId(session); // 투표는 로그인 필수
        VoteStatusDto status = topicService.voteForTopic(memberId, dto);

        return ResponseEntity.ok(
                ApiResponse.success("투표가 성공적으로 반영되었습니다.", status)
        );
    }
}