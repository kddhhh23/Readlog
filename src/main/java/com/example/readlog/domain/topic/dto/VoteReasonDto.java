package com.example.readlog.domain.topic.dto;

import lombok.Builder;
import lombok.Getter;

// 투표 이유 목록 (reasonsList)에 표시될 개별 투표 데이터를 전송하는 DTO
@Getter
@Builder
public class VoteReasonDto {

    // 투표한 사용자의 ID
    private String memberId;

    // 사용자가 선택한 옵션 ('A' 또는 'B')
    private String choice;

    // 사용자가 작성한 투표 이유 (선택 사항)
    private String reason;
}