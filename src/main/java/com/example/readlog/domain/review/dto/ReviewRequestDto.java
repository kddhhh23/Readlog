package com.example.readlog.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    private Long bookId;
    private String content;
    private Integer rating; // 0~5
}
