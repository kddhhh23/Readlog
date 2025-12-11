package com.example.readlog.domain.member.service;

import com.example.readlog.domain.member.dto.MemberRegisterDto;
import com.example.readlog.domain.member.entity.Member;
import com.example.readlog.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 회원 관련 비즈니스 로직 처리 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    // 비밀번호 암호화를 위한 인코더 객체 생성
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입 로직: 비밀번호 암호화 후 저장
    public void registerNewMember(MemberRegisterDto dto) {
        // ID 중복 확인
        if (memberRepository.existsById(dto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // Member 엔티티 생성 및 DB 저장
        Member member = Member.builder()
                .memberId(dto.getMemberId())
                .password(encodedPassword)
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .phoneNumber(dto.getPhoneNumber())
                .school(dto.getSchool())
                .build();

        memberRepository.save(member);
    }

    // 로그인 로직: ID로 찾고 비밀번호 검증
    @Transactional(readOnly = true)
    public Member authenticate(String memberId, String rawPassword) {
        // ID로 사용자 찾기
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return null; // 사용자 없음
        }

        // 비밀번호 비교
        if (passwordEncoder.matches(rawPassword, member.getPassword())) {
            return member; // 인증 성공
        } else {
            return null; // 비밀번호 불일치
        }
    }

    // 아이디 부분 일치로 회원 검색
    @Transactional(readOnly = true)
    public List<Member> searchMembersById(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드를 입력해야 합니다.");
        }
        return memberRepository.findByMemberIdContaining(keyword.trim());
    }

    // 학교 이름으로 회원 검색
    @Transactional(readOnly = true)
    public List<Member> searchMembersBySchool(String schoolName) {
        if (schoolName == null || schoolName.trim().isEmpty()) {
            return List.of();
        }
        return memberRepository.findBySchool(schoolName.trim());
    }

    // 로그인된 사용자와 같은 학교 회원 중 자신을 제외한 목록을 검색
    @Transactional(readOnly = true)
    public List<Member> findSameSchoolMembers(String memberId) {
        // 로그인 사용자 정보 및 학교 이름 조회
        Member currentUser = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다."));

        String schoolName = currentUser.getSchool();

        // 같은 학교의 모든 회원 조회
        List<Member> sameSchoolMembers = memberRepository.findBySchool(schoolName);

        // 자기 자신을 제외하고 반환
        return sameSchoolMembers.stream()
                .filter(m -> !m.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }
}