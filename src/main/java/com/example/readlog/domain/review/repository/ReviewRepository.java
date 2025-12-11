package com.example.readlog.domain.review.repository;

import com.example.readlog.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 책에 대한 모든 리뷰를 최신순으로 조회
    @Query("SELECT r FROM Review r WHERE r.book.bookId = :bookId ORDER BY r.createdAt DESC")
    List<Review> findReviewsByBookId(@Param("bookId") Long bookId);

    // 특정 책에 대한 리뷰의 평균 별점을 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.bookId = :bookId")
    Double calculateAverageRatingByBookId(@Param("bookId") Long bookId);

    // 특정 회원과 특정 책에 대한 리뷰가 이미 있는지 조회 (리뷰 중복 검증)
    @Query("SELECT r FROM Review r WHERE r.member.memberId = :memberId AND r.book.bookId = :bookId")
    Optional<Review> findByMemberIdAndBookId(@Param("memberId") String memberId, @Param("bookId") Long bookId);

    // 특정 회원이 작성한 모든 리뷰를 최신순으로 조회
    @Query("SELECT r FROM Review r WHERE r.member.memberId = :memberId ORDER BY r.createdAt DESC")
    List<Review> findAllByMemberId(@Param("memberId") String memberId);

    // 팔로잉하는 회원들의 리뷰를 최신순으로 조회
    @Query("SELECT r FROM Review r WHERE r.member.memberId IN :memberIds ORDER BY r.createdAt DESC")
    List<Review> findAllByMemberIds(@Param("memberIds") List<String> memberIds);
}