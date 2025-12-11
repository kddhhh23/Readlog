package com.example.readlog.domain.review.entity;

import com.example.readlog.domain.book.entity.Book;
import com.example.readlog.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // FK 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public void update(String content, Integer rating) {
        if (content != null) {
            this.content = content;
        }
        if (rating != null) {
            this.rating = rating;
        }
        // @CreatedDate 대신 @LastModifiedDate가 있었다면 여기에서 자동으로 수정 시간이 기록됩니다.
    }
}
