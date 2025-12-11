package com.example.readlog.domain.review.entity;


import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReviewLikeId implements Serializable {
    private String member; // ReviewLike 엔티티의 변수명과 일치
    private Long review;   // ReviewLike 엔티티의 변수명과 일치
}