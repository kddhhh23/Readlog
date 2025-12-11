package com.example.readlog.domain.readinghistory.entity;

import jakarta.persistence.*;
import lombok.*; // ⭐ [수정] Lombok import에 @Setter 포함
import java.time.LocalDate;
import com.example.readlog.domain.member.entity.Member;
import com.example.readlog.domain.book.entity.Book;

@Entity
@Table(name = "reading_history")
@Getter
@Setter // ⭐ [추가] Setter를 클래스 레벨에 추가합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReadingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_history_id")
    private Long readingHistoryId;

    @Column(name = "read_start_date")
    private LocalDate readStartDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "read_status", nullable = false)
    private ReadStatus readStatus; // ReadStatus는 Enum 타입입니다.

    @Column(columnDefinition = "TEXT")
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 기존 update 메서드는 그대로 유지
    public void update(LocalDate readStartDate, ReadStatus readStatus, String memo) {
        if (readStartDate != null) {
            this.readStartDate = readStartDate;
        }
        if (readStatus != null) {
            this.readStatus = readStatus;
        }
        if (memo != null) {
            this.memo = memo;
        }
    }

    // ⭐ @Setter 추가로 인해 public void setReadStatus(ReadStatus readStatus) {}가 자동으로 생성됩니다.
}