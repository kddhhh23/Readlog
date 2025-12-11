package com.example.readlog.domain.topic.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.readlog.domain.member.entity.Member;

@Entity
@Table(name = "vote", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_topic", columnNames = {"member_id", "topic_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long voteId;

    @Column(nullable = false, length = 1)
    private String choice; // 'A' or 'B' (Enum으로 처리해도 좋음)

    @Column(length = 255)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
}
