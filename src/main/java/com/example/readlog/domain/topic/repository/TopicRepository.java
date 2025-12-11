package com.example.readlog.domain.topic.repository;

import com.example.readlog.domain.topic.entity.Topic;
import com.example.readlog.domain.topic.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    // 오늘 날짜의 토픽을 조회
    @Query("SELECT t FROM Topic t WHERE t.createdDate = :today")
    Optional<Topic> findTodayTopic(@Param("today") LocalDate today);
}