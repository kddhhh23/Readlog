package com.example.readlog.domain.readinghistory.dto;

import com.example.readlog.domain.readinghistory.entity.ReadStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
public class ReadingHistoryResponseDto {
        private Long readingHistoryId;
        private LocalDate readStartDate;
        private ReadStatus readStatus;
        private String memo;
        private String memberId; // 기록한 회원 ID
        private String bookTitle; // 기록된 책 제목
        private String bookAuthor; // 기록된 책 작가
}
