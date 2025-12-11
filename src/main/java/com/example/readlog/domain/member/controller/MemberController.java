package com.example.readlog.domain.member.controller;

import com.example.readlog.domain.member.dto.MemberLoginDto;
import com.example.readlog.domain.member.dto.MemberRegisterDto;
import com.example.readlog.domain.member.entity.Member;
import com.example.readlog.domain.member.service.MemberService;
import com.example.readlog.global.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// 회원 인증 및 회원 정보 조회 API를 처리하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private static final String MEMBER_ID_SESSION_KEY = "LOGGED_IN_MEMBER_ID";

    // 새로운 회원을 등록 (회원가입)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody MemberRegisterDto dto) {
        memberService.registerNewMember(dto);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    // 사용자 인증 후 세션에 로그인 ID 저장 (로그인)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody MemberLoginDto dto, HttpSession session) {
        Member authenticatedMember = memberService.authenticate(dto.getMemberId(), dto.getPassword());

        if (authenticatedMember != null) {
            session.setAttribute(MEMBER_ID_SESSION_KEY, authenticatedMember.getMemberId());
            return ResponseEntity.ok(
                    ApiResponse.success(authenticatedMember.getMemberId() + "님, 로그인 성공!")
            );
        } else {
            return ResponseEntity
                    .status(401)
                    .body(ApiResponse.error("아이디 또는 비밀번호가 일치하지 않습니다."));
        }
    }

    // 세션을 무효화하여 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
    }

    // 키워드로 회원 ID를 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchMembers(@RequestParam String keyword) {
        List<Member> members = memberService.searchMembersById(keyword);

        List<String> memberIds = members.stream()
                .map(Member::getMemberId)
                .collect(Collectors.toList());

        if (memberIds.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("검색 결과가 없습니다.", memberIds));
        }

        return ResponseEntity.ok(ApiResponse.success(
                "'" + keyword + "'에 대한 회원 검색 결과입니다. (총 " + memberIds.size() + "명)", memberIds));
    }

    // 로그인된 사용자와 같은 학교 회원 목록 조회
    @GetMapping("/school")
    public ResponseEntity<ApiResponse<?>> getSchoolMembers(HttpSession session) {
        String memberId = (String) session.getAttribute(MEMBER_ID_SESSION_KEY);
        if (memberId == null) {
            throw new IllegalStateException("로그인된 사용자만 이용할 수 있습니다.");
        }

        List<Member> members = memberService.findSameSchoolMembers(memberId);

        List<String> memberIds = members.stream()
                .map(Member::getMemberId)
                .collect(Collectors.toList());

        if (memberIds.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("같은 학교 회원이 없습니다.", memberIds));
        }

        return ResponseEntity.ok(ApiResponse.success(
                "같은 학교 회원 " + memberIds.size() + "명을 조회했습니다.", memberIds));
    }
}