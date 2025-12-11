package com.example.readlog.domain.topic.service;

import com.example.readlog.domain.member.entity.Member;
import com.example.readlog.domain.member.repository.MemberRepository;
import com.example.readlog.domain.topic.converter.TopicConverter;
import com.example.readlog.domain.topic.dto.VoteReasonDto;
import com.example.readlog.domain.topic.dto.VoteRequestDto;
import com.example.readlog.domain.topic.dto.VoteStatusDto;
import com.example.readlog.domain.topic.entity.Topic;
import com.example.readlog.domain.topic.entity.Vote;
import com.example.readlog.domain.topic.repository.TopicRepository;
import com.example.readlog.domain.topic.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 오늘의 질문 관련 비즈니스 로직 처리 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class TopicService {

    private final TopicRepository topicRepository;
    private final VoteRepository voteRepository;
    private final MemberRepository memberRepository;
    private final TopicConverter topicConverter;

    // 오늘의 질문, 투표 상태 및 결과를 조회
    @Transactional(readOnly = true)
    public VoteStatusDto getTodayTopicStatus(String memberId) {
        // 1. 오늘의 질문 조회
        Topic todayTopic = topicRepository.findTodayTopic(LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("오늘의 질문이 아직 등록되지 않았습니다."));

        Long topicId = todayTopic.getTopicId();

        // 2. 투표 결과 집계 (총 투표 수, A/B 카운트)
        Map<String, Long> voteCounts = voteRepository.countVotesByTopicId(topicId).stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));

        long totalVotes = voteCounts.values().stream().mapToLong(Long::longValue).sum();

        // 3. 사용자 투표 상태 확인
        String myChoice = voteRepository.findByMemberIdAndTopicId(memberId, topicId)
                .map(Vote::getChoice)
                .orElse(null);

        // 4. 투표 이유 목록 조회 및 DTO 변환
        List<Vote> allVotes = voteRepository.findAllByTopicId(topicId);

        List<VoteReasonDto> reasons = allVotes.stream()
                .map(vote -> VoteReasonDto.builder()
                        .memberId(vote.getMember().getMemberId())
                        .choice(vote.getChoice())
                        .reason(vote.getReason())
                        .build())
                .collect(Collectors.toList());

        // 5. 최종 DTO로 변환하여 반환
        return VoteStatusDto.builder()
                .topic(topicConverter.convert(todayTopic))
                .myChoice(myChoice)
                .voteCounts(voteCounts)
                .totalVotes(totalVotes)
                .reasons(reasons)
                .build();
    }

    // 투표를 생성 및 처리
    public VoteStatusDto voteForTopic(String memberId, VoteRequestDto dto) {
        Long topicId = dto.getTopicId();

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 중복 투표 검증
        if (voteRepository.findByMemberIdAndTopicId(memberId, topicId).isPresent()) {
            throw new IllegalStateException("이미 투표를 완료했습니다.");
        }

        // 투표 생성 및 저장
        Vote vote = Vote.builder()
                .member(member)
                .topic(topic)
                .choice(dto.getChoice())
                .reason(dto.getReason())
                .build();

        voteRepository.save(vote);

        // 투표 후 최신 상태 반환
        return getTodayTopicStatus(memberId);
    }
}