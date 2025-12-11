package com.example.readlog.domain.topic.repository;

import com.example.readlog.domain.topic.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 특정 회원이 특정 토픽에 투표했는지 확인
    @Query("SELECT v FROM Vote v WHERE v.member.memberId = :memberId AND v.topic.topicId = :topicId")
    Optional<Vote> findByMemberIdAndTopicId(@Param("memberId") String memberId, @Param("topicId") Long topicId);

    // 특정 토픽의 선택지별 투표 결과를 집계
    @Query("SELECT v.choice, COUNT(v) FROM Vote v WHERE v.topic.topicId = :topicId GROUP BY v.choice")
    List<Object[]> countVotesByTopicId(@Param("topicId") Long topicId);

    // 특정 토픽에 대한 모든 투표 목록을 조회
    @Query("SELECT v FROM Vote v WHERE v.topic.topicId = :topicId")
    List<Vote> findAllByTopicId(@Param("topicId") Long topicId);
}