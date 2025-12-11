package com.example.readlog.domain.review.repository;

import com.example.readlog.domain.review.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 특정 리뷰에 달린 모든 답글을 생성 순서대로 조회
    @Query("SELECT rp FROM Reply rp WHERE rp.review.reviewId = :reviewId ORDER BY rp.createdAt ASC")
    List<Reply> findRepliesByReviewId(@Param("reviewId") Long reviewId);
}