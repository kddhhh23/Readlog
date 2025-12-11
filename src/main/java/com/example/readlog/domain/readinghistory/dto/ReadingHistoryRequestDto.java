package com.example.readlog.domain.readinghistory.dto;

import com.example.readlog.domain.readinghistory.entity.ReadStatus;
import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
public class ReadingHistoryRequestDto {
    private Long bookId; // 기록할 책 ID
    private LocalDate readStartDate;
    private ReadStatus readStatus; // READING, COMPLETED, STOPPED 중 하나
    private String memo;
}
