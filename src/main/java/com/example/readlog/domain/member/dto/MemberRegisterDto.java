package com.example.readlog.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

// 회원가입 요청 시 사용 DTO
@Getter
@Setter
public class MemberRegisterDto {
    private String memberId;
    private String password;
    private String name;
    private String email;
    private Integer age;
    private String phoneNumber;
    private String school;
}