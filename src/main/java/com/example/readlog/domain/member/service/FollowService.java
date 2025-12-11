package com.example.readlog.domain.member.service;

import com.example.readlog.domain.member.entity.Member;
import com.example.readlog.domain.member.repository.MemberRepository;
import com.example.readlog.domain.member.entity.Follow;
import com.example.readlog.domain.member.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    // 팔로우 관계를 토글 (팔로우 또는 언팔로우)
    public boolean toggleFollow(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팔로워 ID입니다."));
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팔로잉 대상 ID입니다."));

        Optional<Follow> existingFollow = followRepository.findByFollower_MemberIdAndFollowing_MemberId(followerId, followingId);

        if (existingFollow.isPresent()) {
            // 언팔로우 (삭제)
            followRepository.delete(existingFollow.get());
            return false;
        } else {
            // 팔로우 (생성)
            Follow newFollow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            followRepository.save(newFollow);
            return true;
        }
    }

    // 특정 회원의 팔로잉 목록 (내가 팔로우하는 ID 목록)을 조회
    @Transactional(readOnly = true)
    public List<String> getFollowingList(String memberId) {
        return followRepository.findFollowingIdsByFollowerId(memberId);
    }

    // 특정 회원의 팔로워 목록 (나를 팔로우하는 ID 목록)을 조회
    @Transactional(readOnly = true)
    public List<String> getFollowerList(String memberId) {
        return followRepository.findFollowerIdsByFollowingId(memberId);
    }

    // 특정 회원의 팔로워 및 팔로잉 수를 조회
    @Transactional(readOnly = true)
    public Map<String, Long> getFollowCounts(String memberId) {
        Long followingCount = followRepository.countFollowingByMemberId(memberId);
        Long followerCount = followRepository.countFollowerByMemberId(memberId);

        return Map.of(
                "followingCount", followingCount,
                "followerCount", followerCount
        );
    }
}