package com.example.readlog.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;

    // 책 정보 필드
    private Long bookId;
    private String bookTitle;

    private String memberId;
    private String memberName; // 작성자 이름
    private Long likeCount; // 좋아요 수
    private List<ReplyResponseDto> replies; // 답글 리스트

    @JsonProperty("isMine")
    private boolean isMine;
}