package com.example.readlog.domain.topic.converter;

import com.example.readlog.domain.topic.dto.TopicResponseDto;
import com.example.readlog.domain.topic.entity.Topic;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// Topic 엔티티를 응답 DTO로 변환하는 컨버터
@Component
public class TopicConverter implements Converter<Topic, TopicResponseDto> {

    // Topic 엔티티를 TopicResponseDto로 변환
    @Override
    public TopicResponseDto convert(Topic source) {
        if (source == null) {
            return null;
        }

        return TopicResponseDto.builder()
                .topicId(source.getTopicId())
                .question(source.getQuestion())
                .optionA(source.getOptionA())
                .optionB(source.getOptionB())
                .createdDate(source.getCreatedDate())
                .build();
    }
}