package com.example.readlog.domain.review.service;

import com.example.readlog.domain.book.entity.Book;
import com.example.readlog.domain.book.repository.BookRepository;
import com.example.readlog.domain.member.entity.Member;
import com.example.readlog.domain.member.repository.MemberRepository;
import com.example.readlog.domain.member.service.FollowService;
import com.example.readlog.domain.review.converter.ReplyConverter;
import com.example.readlog.domain.review.converter.ReviewConverter;
import com.example.readlog.domain.review.dto.ReplyRequestDto;
import com.example.readlog.domain.review.dto.ReplyResponseDto;
import com.example.readlog.domain.review.dto.ReviewRequestDto;
import com.example.readlog.domain.review.dto.ReviewResponseDto;
import com.example.readlog.domain.review.entity.Reply;
import com.example.readlog.domain.review.entity.Review;
import com.example.readlog.domain.review.entity.ReviewLike;
import com.example.readlog.domain.review.repository.ReplyRepository;
import com.example.readlog.domain.review.repository.ReviewLikeRepository;
import com.example.readlog.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 리뷰 및 답글 관련 비즈니스 로직 처리 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReplyRepository replyRepository;
    private final ReviewLikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ReplyConverter replyConverter;
    private final ReviewConverter reviewConverter;
    private final FollowService followService;

    // --- 리뷰 (Review) 기능 ---

    // 특정 책의 리뷰 목록과 통계를 조회
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByBook(Long bookId, String currentLoggedInMemberId) {
        List<Review> reviews = reviewRepository.findReviewsByBookId(bookId);

        return reviews.stream()
                .map(review -> {
                    // 좋아요 수 계산
                    Long likeCount = likeRepository.countByReviewId(review.getReviewId());

                    // 답글 목록 조회 및 DTO 변환
                    List<ReplyResponseDto> replies = replyRepository.findRepliesByReviewId(review.getReviewId()).stream()
                            .map(reply -> replyConverter.convertToDto(reply, currentLoggedInMemberId))
                            .collect(Collectors.toList());

                    // 리뷰 정보와 통계를 DTO로 변환
                    return reviewConverter.convertToDto(review, likeCount, replies, currentLoggedInMemberId);
                })
                .collect(Collectors.toList());
    }

    // 특정 회원이 작성한 모든 리뷰를 조회 (내 리뷰 보기)
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getMyReviews(String memberId) {
        // 특정 memberId로 리뷰 목록 조회
        List<Review> reviews = reviewRepository.findAllByMemberId(memberId);

        // DTO로 변환
        return reviews.stream()
                .map(review -> {
                    // 좋아요 수 계산 및 답글 목록 없이 DTO로 변환
                    Long likeCount = likeRepository.countByReviewId(review.getReviewId());
                    return reviewConverter.convertToDto(review, likeCount, List.of(), memberId);
                })
                .collect(Collectors.toList());
    }

    // 팔로잉하는 회원들의 리뷰 목록을 조회 (팔로잉 피드)
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getFollowingReviews(String currentMemberId) {
        // 팔로잉하는 회원들의 ID 목록을 가져옴
        List<String> followingIds = followService.getFollowingList(currentMemberId);

        if (followingIds.isEmpty()) {
            return List.of();
        }

        // 해당 ID들의 리뷰를 최신순으로 조회 및 DTO로 변환
        List<Review> reviews = reviewRepository.findAllByMemberIds(followingIds);

        return reviews.stream()
                .map(review -> {
                    // 좋아요 수 계산 및 답글 목록 없이 DTO로 변환
                    Long likeCount = likeRepository.countByReviewId(review.getReviewId());
                    return reviewConverter.convertToDto(review, likeCount, List.of(), currentMemberId);
                })
                .collect(Collectors.toList());
    }


    // 리뷰 생성
    public ReviewResponseDto createReview(String memberId, ReviewRequestDto dto) {
        // 회원 및 책 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        // 중복 리뷰 검증
        Optional<Review> existingReview = reviewRepository.findByMemberIdAndBookId(memberId, dto.getBookId());
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("이미 해당 책에 대한 리뷰를 작성했습니다. 기존 리뷰를 수정하거나 삭제해 주세요.");
        }

        // 리뷰 엔티티 생성 및 저장
        Review review = Review.builder()
                .content(dto.getContent())
                .rating(dto.getRating())
                .member(member)
                .book(book)
                .build();

        Review savedReview = reviewRepository.save(review);

        // DTO로 변환 후 반환
        return reviewConverter.convertToDto(savedReview, 0L, List.of(), memberId);
    }

    // 리뷰 수정
    public ReviewResponseDto updateReview(Long reviewId, String memberId, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // 권한 검증
        if (!review.getMember().getMemberId().equals(memberId)) {
            throw new IllegalAccessError("리뷰를 수정할 권한이 없습니다.");
        }

        // 내용 및 평점 업데이트
        review.update(dto.getContent(), dto.getRating());

        // 수정 후 좋아요 수와 답글을 다시 로드하여 DTO 생성
        Long likeCount = likeRepository.countByReviewId(reviewId);

        List<ReplyResponseDto> replies = replyRepository.findRepliesByReviewId(reviewId).stream()
                .map(reply -> replyConverter.convertToDto(reply, memberId))
                .collect(Collectors.toList());

        return reviewConverter.convertToDto(review, likeCount, replies, memberId);
    }

    // 리뷰 삭제
    public void deleteReview(Long reviewId, String memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // 권한 검증
        if (!review.getMember().getMemberId().equals(memberId)) {
            throw new IllegalAccessError("리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }

    // --- 답글 (Reply) 기능 ---

    // 답글 생성
    public ReplyResponseDto createReply(Long reviewId, String memberId, ReplyRequestDto dto) {
        // 회원 및 리뷰 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // 답글 엔티티 생성 및 저장
        Reply reply = Reply.builder()
                .content(dto.getContent())
                .member(member)
                .review(review)
                .build();

        // 답글 DTO로 변환 후 반환
        return replyConverter.convertToDto(replyRepository.save(reply), memberId);
    }

    // 답글 삭제
    public void deleteReply(Long replyId, String memberId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답글입니다."));

        // 권한 검증
        if (!reply.getMember().getMemberId().equals(memberId)) {
            throw new IllegalAccessError("답글을 삭제할 권한이 없습니다.");
        }

        replyRepository.delete(reply);
    }

    // --- 좋아요 (ReviewLike) 기능 ---

    // 리뷰 좋아요/취소 토글 (true: 좋아요 추가, false: 좋아요 취소)
    public boolean toggleLike(Long reviewId, String memberId) {
        // 리뷰 및 회원 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 좋아요 기록 확인
        Optional<ReviewLike> existingLike = likeRepository.findReviewLikeByMemberIdAndReviewId(memberId, reviewId);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 있다면: 취소 (삭제)
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            // 좋아요가 없다면: 추가 (생성)
            ReviewLike newLike = ReviewLike.builder()
                    .member(member)
                    .review(review)
                    .build();
            likeRepository.save(newLike);
            return true;
        }
    }

    // 특정 책의 평균 별점 조회
    @Transactional(readOnly = true)
    public Double getAverageRating(Long bookId) {
        return reviewRepository.calculateAverageRatingByBookId(bookId);
    }
}