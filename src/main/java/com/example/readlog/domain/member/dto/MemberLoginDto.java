package com.example.readlog.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

// 로그인 요청 시 사용 DTO
@Getter
@Setter
public class MemberLoginDto {
    private String memberId;
    private String password;
}