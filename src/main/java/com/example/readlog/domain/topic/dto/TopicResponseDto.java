package com.example.readlog.domain.topic.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// 오늘의 질문 정보를 담는 응답 DTO
@Getter
@Builder
public class TopicResponseDto {
    private Long topicId;
    private String question;
    private String optionA;
    private String optionB;
    private LocalDate createdDate;


}