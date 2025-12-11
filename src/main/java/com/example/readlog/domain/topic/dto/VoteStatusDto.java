package com.example.readlog.domain.topic.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class VoteStatusDto {
    private TopicResponseDto topic; // 현재 질문 정보
    private String myChoice; // 내 투표 결과 ('A', 'B')
    private Map<String, Long> voteCounts; // 투표 수 { "A": 10, "B": 5 }
    private Long totalVotes; // 총 투표 수
    private List<VoteReasonDto> reasons;
}