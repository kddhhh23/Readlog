package com.example.readlog.domain.member.repository;

import com.example.readlog.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // 회원 ID가 키워드를 포함하는 회원을 검색
    List<Member> findByMemberIdContaining(String memberId);

    // 학교 이름으로 회원 목록을 검색
    List<Member> findBySchool(String school);
}