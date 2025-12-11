package com.example.readlog.domain.topic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteRequestDto {
    private Long topicId;
    private String choice; // 'A' 또는 'B'
    private String reason;
}