package com.example.readlog.domain.topic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "topic")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // 생성일 자동 주입용
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;

    @Column(nullable = false)
    private String question;

    @Column(name = "option_a", nullable = false, length = 100)
    private String optionA;

    @Column(name = "option_b", nullable = false, length = 100)
    private String optionB;

    @CreatedDate
    @Column(name = "created_date")
    private LocalDate createdDate;
}