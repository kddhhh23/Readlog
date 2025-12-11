package com.example.readlog.domain.review.repository;

import com.example.readlog.domain.review.entity.ReviewLike;
import com.example.readlog.domain.review.entity.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    // 특정 회원과 리뷰에 대한 좋아요 기록이 있는지 조회
    @Query("SELECT rl FROM ReviewLike rl WHERE rl.member.memberId = :memberId AND rl.review.reviewId = :reviewId")
    Optional<ReviewLike> findReviewLikeByMemberIdAndReviewId(@Param("memberId") String memberId, @Param("reviewId") Long reviewId);

    // 특정 리뷰의 총 좋아요 수를 계산
    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.reviewId = :reviewId")
    Long countByReviewId(@Param("reviewId") Long reviewId);
}