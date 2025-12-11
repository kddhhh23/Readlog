package com.example.readlog.domain.member.controller;

import com.example.readlog.domain.member.service.FollowService;
import com.example.readlog.global.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 팔로우/언팔로우 및 팔로우 목록 조회를 처리하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;
    private static final String MEMBER_ID_SESSION_KEY = "LOGGED_IN_MEMBER_ID";

    // 세션에서 현재 로그인된 사용자 ID를 가져옴
    private String getMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute(MEMBER_ID_SESSION_KEY);
        if (memberId == null) {
            throw new IllegalStateException("로그인된 사용자만 이용할 수 있습니다.");
        }
        return memberId;
    }

    // 팔로우/언팔로우 상태를 토글
    @PostMapping("/{followingId}")
    public ResponseEntity<ApiResponse<?>> toggleFollow(@PathVariable String followingId, HttpSession session) {
        String followerId = getMemberId(session);

        boolean isFollowing = followService.toggleFollow(followerId, followingId);

        Map<String, Object> data = Map.of(
                "followerId", followerId,
                "followingId", followingId,
                "isFollowing", isFollowing
        );

        String message = isFollowing ? followingId + "님을 팔로우했습니다." : followingId + "님을 언팔로우했습니다.";

        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    // 특정 회원이 팔로우하는 목록 (Following) 조회
    @GetMapping("/following/{memberId}")
    public ResponseEntity<ApiResponse<?>> getFollowing(@PathVariable String memberId) {
        List<String> followingList = followService.getFollowingList(memberId);
        return ResponseEntity.ok(ApiResponse.success("팔로잉 목록을 성공적으로 조회했습니다.", followingList));
    }

    // 특정 회원을 팔로우하는 목록 (Follower) 조회
    @GetMapping("/follower/{memberId}")
    public ResponseEntity<ApiResponse<?>> getFollowers(@PathVariable String memberId) {
        List<String> followerList = followService.getFollowerList(memberId);
        return ResponseEntity.ok(ApiResponse.success("팔로워 목록을 성공적으로 조회했습니다.", followerList));
    }

    // 특정 회원의 팔로잉/팔로워 수 조회
    @GetMapping("/counts/{memberId}")
    public ResponseEntity<ApiResponse<?>> getFollowCounts(@PathVariable String memberId) {
        Map<String, Long> counts = followService.getFollowCounts(memberId);
        return ResponseEntity.ok(ApiResponse.success("팔로우 수를 성공적으로 조회했습니다.", counts));
    }
}