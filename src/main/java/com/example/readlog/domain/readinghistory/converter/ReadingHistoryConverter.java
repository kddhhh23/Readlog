package com.example.readlog.domain.readinghistory.converter;

import com.example.readlog.domain.readinghistory.dto.ReadingHistoryResponseDto;
import com.example.readlog.domain.readinghistory.entity.ReadingHistory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// ReadingHistory 엔티티를 응답 DTO로 변환하는 컨버터
@Component
public class ReadingHistoryConverter implements Converter<ReadingHistory, ReadingHistoryResponseDto> {

    // ReadingHistory 엔티티를 DTO로 변환
    @Override
    public ReadingHistoryResponseDto convert(ReadingHistory source) {
        if (source == null) {
            return null;
        }

        return ReadingHistoryResponseDto.builder()
                .readingHistoryId(source.getReadingHistoryId())
                .readStartDate(source.getReadStartDate())
                .readStatus(source.getReadStatus())
                .memo(source.getMemo())
                // 연관된 Member와 Book 엔티티에서 ID, Title, Author를 가져와 매핑
                .memberId(source.getMember().getMemberId())
                .bookTitle(source.getBook().getTitle())
                .bookAuthor(source.getBook().getAuthor())
                .build();
    }
}