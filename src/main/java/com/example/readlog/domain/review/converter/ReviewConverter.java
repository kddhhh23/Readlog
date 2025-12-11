package com.example.readlog.domain.review.converter;

import com.example.readlog.domain.review.dto.ReviewResponseDto;
import com.example.readlog.domain.review.dto.ReplyResponseDto;
import com.example.readlog.domain.review.entity.Review;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewConverter {

    // Review 엔티티와 관련 통계를 ReviewResponseDto로 변환
    public ReviewResponseDto convertToDto(
            Review review,
            Long likeCount,
            List<ReplyResponseDto> replies,
            String currentLoggedInMemberId
    ) {
        if (review == null) {
            return null;
        }

        // 현재 사용자가 작성자인지 확인
        boolean isMine = false;
        if (currentLoggedInMemberId != null && review.getMember() != null) {
            isMine = review.getMember().getMemberId().equals(currentLoggedInMemberId);
        }

        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())

                // 책 정보 필드 매핑
                .bookId(review.getBook().getBookId())
                .bookTitle(review.getBook().getTitle())

                .memberId(review.getMember().getMemberId())
                .memberName(review.getMember().getName())
                .likeCount(likeCount)
                .replies(replies)
                .isMine(isMine)
                .build();
    }
}