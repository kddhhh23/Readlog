package com.example.readlog.domain.review.controller;

import com.example.readlog.domain.review.dto.ReplyRequestDto;
import com.example.readlog.domain.review.dto.ReplyResponseDto;
import com.example.readlog.domain.review.dto.ReviewRequestDto;
import com.example.readlog.domain.review.dto.ReviewResponseDto;
import com.example.readlog.domain.review.service.ReviewService;
import com.example.readlog.global.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 리뷰 및 답글 관련 API를 처리하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private static final String MEMBER_ID_SESSION_KEY = "LOGGED_IN_MEMBER_ID";

    // 세션에서 로그인된 사용자 ID를 가져옴 (없으면 예외 발생)
    private String getMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute(MEMBER_ID_SESSION_KEY);
        if (memberId == null) {
            throw new IllegalStateException("로그인된 사용자만 이용할 수 있습니다.");
        }
        return memberId;
    }

    // 세션에서 로그인된 사용자 ID를 가져옴 (없으면 null 반환)
    private String getMemberIdIfPresent(HttpSession session) {
        return (String) session.getAttribute(MEMBER_ID_SESSION_KEY);
    }

    // 특정 책의 리뷰 목록을 조회 (비로그인 사용자도 가능)
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getReviews(@RequestParam Long bookId, HttpSession session) {
        String memberId = getMemberIdIfPresent(session);
        List<ReviewResponseDto> reviews = reviewService.getReviewsByBook(bookId, memberId);
        String message = reviews.isEmpty() ? "아직 리뷰가 없습니다." : "총 " + reviews.size() + "개의 리뷰를 조회했습니다.";

        return ResponseEntity.ok(ApiResponse.success(message, reviews));
    }

    // 현재 로그인 사용자의 리뷰 목록을 조회
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyReviews(HttpSession session) {
        String memberId = getMemberId(session);
        List<ReviewResponseDto> reviews = reviewService.getMyReviews(memberId);
        String message = reviews.isEmpty() ? "작성된 리뷰가 없습니다." : "총 " + reviews.size() + "개의 리뷰를 조회했습니다.";

        return ResponseEntity.ok(ApiResponse.success(message, reviews));
    }

    // 팔로잉하는 사용자들의 리뷰 목록을 조회
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<?>> getFollowingReviews(HttpSession session) {
        String memberId = getMemberId(session);
        List<ReviewResponseDto> reviews = reviewService.getFollowingReviews(memberId);
        String message = reviews.isEmpty()
                ? "팔로잉하는 사용자들의 리뷰가 없습니다."
                : "총 " + reviews.size() + "개의 팔로잉 리뷰를 조회했습니다.";

        return ResponseEntity.ok(ApiResponse.success(message, reviews));
    }

    // 새로운 리뷰를 작성
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createReview(@RequestBody ReviewRequestDto dto, HttpSession session) {
        String memberId = getMemberId(session);
        ReviewResponseDto response = reviewService.createReview(memberId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("리뷰가 성공적으로 작성되었습니다.", response)
        );
    }

    // 기존 리뷰를 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequestDto dto, HttpSession session) {
        String memberId = getMemberId(session);
        ReviewResponseDto response = reviewService.updateReview(reviewId, memberId, dto);

        return ResponseEntity.ok(ApiResponse.success("리뷰가 성공적으로 수정되었습니다.", response));
    }

    // 특정 리뷰를 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deleteReview(@PathVariable Long reviewId, HttpSession session) {
        String memberId = getMemberId(session);
        reviewService.deleteReview(reviewId, memberId);

        return ResponseEntity.ok(ApiResponse.success("리뷰가 성공적으로 삭제되었습니다."));
    }

    // 특정 책의 평균 별점을 조회
    @GetMapping("/rating")
    public ResponseEntity<ApiResponse<?>> getAverageRating(@RequestParam Long bookId) {
        Double average = reviewService.getAverageRating(bookId);
        return ResponseEntity.ok(ApiResponse.success("평균 별점 조회 성공", average != null ? average : 0.0));
    }

    // 특정 리뷰에 답글을 작성
    @PostMapping("/{reviewId}/replies")
    public ResponseEntity<ApiResponse<?>> createReply(@PathVariable Long reviewId, @RequestBody ReplyRequestDto dto, HttpSession session) {
        String memberId = getMemberId(session);
        ReplyResponseDto response = reviewService.createReply(reviewId, memberId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("답글이 성공적으로 작성되었습니다.", response)
        );
    }

    // 특정 답글을 삭제
    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<ApiResponse<?>> deleteReply(@PathVariable Long replyId, HttpSession session) {
        String memberId = getMemberId(session);
        reviewService.deleteReply(replyId, memberId);

        return ResponseEntity.ok(ApiResponse.success("답글이 성공적으로 삭제되었습니다."));
    }

    // 특정 리뷰에 좋아요/취소 토글
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ApiResponse<?>> toggleLike(@PathVariable Long reviewId, HttpSession session) {
        String memberId = getMemberId(session);
        boolean isLiked = reviewService.toggleLike(reviewId, memberId);

        String message = isLiked ? "좋아요를 추가했습니다." : "좋아요를 취소했습니다.";

        return ResponseEntity.ok(
                ApiResponse.success(message, Map.of(
                        "reviewId", reviewId,
                        "memberId", memberId,
                        "status", isLiked ? "LIKED" : "UNLIKED"
                ))
        );
    }
}