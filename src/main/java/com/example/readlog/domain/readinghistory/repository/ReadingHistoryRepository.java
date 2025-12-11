package com.example.readlog.domain.readinghistory.repository;

import com.example.readlog.domain.readinghistory.entity.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {

    // 특정 회원의 모든 독서 기록을 최신순으로 조회
    @Query("SELECT h FROM ReadingHistory h WHERE h.member.memberId = :memberId ORDER BY h.readingHistoryId DESC")
    List<ReadingHistory> findHistoriesByMemberId(@Param("memberId") String memberId);

    // 특정 회원과 특정 책에 대한 독서 기록이 있는지 조회
    @Query("SELECT h FROM ReadingHistory h WHERE h.member.memberId = :memberId AND h.book.bookId = :bookId")
    Optional<ReadingHistory> findHistoryByMemberIdAndBookId(@Param("memberId") String memberId, @Param("bookId") Long bookId);

    // 팔로잉하는 여러 회원의 독서 기록을 최신순으로 조회
    @Query("SELECT h FROM ReadingHistory h WHERE h.member.memberId IN :memberIds ORDER BY h.readingHistoryId DESC")
    List<ReadingHistory> findHistoriesByMemberIds(@Param("memberIds") List<String> memberIds);
}