package com.example.readlog.domain.review.converter;

import com.example.readlog.domain.review.dto.ReplyResponseDto;
import com.example.readlog.domain.review.entity.Reply;
import org.springframework.stereotype.Component;

// Reply 엔티티를 DTO로 변환하는 컨버터
@Component
public class ReplyConverter {

    // Reply 엔티티를 ReplyResponseDto로 변환
    public ReplyResponseDto convertToDto(Reply reply, String currentLoggedInMemberId) {
        if (reply == null) {
            return null;
        }

        // isMine 로직 계산
        boolean isMine = false;
        if (currentLoggedInMemberId != null && reply.getMember() != null) {
            isMine = reply.getMember().getMemberId().equals(currentLoggedInMemberId);
        }

        return ReplyResponseDto.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                // Member 엔티티에서 ID와 Name을 가져옴
                .memberId(reply.getMember().getMemberId())
                .memberName(reply.getMember().getName())
                .isMine(isMine) // isMine 필드 매핑
                .build();
    }
}