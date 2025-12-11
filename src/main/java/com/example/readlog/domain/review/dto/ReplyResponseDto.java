package com.example.readlog.domain.review.dto;

import com.example.readlog.domain.review.entity.Reply;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReplyResponseDto {
    private Long replyId;
    private String content;
    private LocalDateTime createdAt;
    private String memberId;
    private String memberName;

    @JsonProperty("isMine")
    private boolean isMine;
}