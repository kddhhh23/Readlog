package com.example.readlog.domain.member.repository;

import com.example.readlog.domain.member.entity.Follow;
import com.example.readlog.domain.member.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    // 특정 회원의 팔로잉 목록 ID만 조회
    @Query("SELECT f.following.memberId FROM Follow f WHERE f.follower.memberId = :followerId")
    List<String> findFollowingIdsByFollowerId(@Param("followerId") String followerId);

    // 특정 회원의 팔로워 목록 ID만 조회
    @Query("SELECT f.follower.memberId FROM Follow f WHERE f.following.memberId = :followingId")
    List<String> findFollowerIdsByFollowingId(@Param("followingId") String followingId);

    // 팔로우 관계 존재 여부를 확인
    Optional<Follow> findByFollower_MemberIdAndFollowing_MemberId(String followerId, String followingId);

    // 특정 회원의 팔로잉 수를 계산
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.memberId = :memberId")
    Long countFollowingByMemberId(@Param("memberId") String memberId);

    // 특정 회원의 팔로워 수를 계산
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following.memberId = :memberId")
    Long countFollowerByMemberId(@Param("memberId") String memberId);
}