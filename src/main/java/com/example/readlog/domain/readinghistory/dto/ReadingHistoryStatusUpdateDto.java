package com.example.readlog.domain.readinghistory.dto;

import com.example.readlog.domain.readinghistory.entity.ReadStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadingHistoryStatusUpdateDto {
    // ReadStatus는 enum 타입(예: READING, COMPLETED, STOPPED)
    private ReadStatus readStatus;

}