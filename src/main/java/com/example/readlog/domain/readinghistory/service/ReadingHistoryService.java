package com.example.readlog.domain.readinghistory.service;

import com.example.readlog.domain.book.entity.Book;
import com.example.readlog.domain.book.repository.BookRepository;
import com.example.readlog.domain.readinghistory.dto.ReadingHistoryRequestDto;
import com.example.readlog.domain.readinghistory.dto.ReadingHistoryResponseDto;
import com.example.readlog.domain.readinghistory.entity.ReadingHistory;
import com.example.readlog.domain.readinghistory.repository.ReadingHistoryRepository;
import com.example.readlog.domain.member.entity.Member;
import com.example.readlog.domain.member.repository.MemberRepository;
import com.example.readlog.domain.readinghistory.converter.ReadingHistoryConverter;
import com.example.readlog.domain.member.service.FollowService;
import com.example.readlog.domain.readinghistory.dto.ReadingHistoryStatusUpdateDto;
import com.example.readlog.domain.readinghistory.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// 독서 기록 관련 비즈니스 로직 처리 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class ReadingHistoryService {

    private final ReadingHistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final ReadingHistoryConverter historyConverter;
    private final FollowService followService;

    // 새로운 독서 기록을 생성
    public ReadingHistoryResponseDto createHistory(String memberId, ReadingHistoryRequestDto dto) {
        // 회원 및 책 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책 ID입니다."));

        // 중복 기록 확인
        if (historyRepository.findHistoryByMemberIdAndBookId(memberId, dto.getBookId()).isPresent()) {
            throw new IllegalArgumentException("이미 해당 책에 대한 독서 기록이 존재합니다. 독서 기록 관리 페이지에서 수정해 주세요.");
        }

        // 독서 기록 엔티티 생성 및 저장
        ReadingHistory newHistory = ReadingHistory.builder()
                .member(member)
                .book(book)
                .readStartDate(dto.getReadStartDate() != null ? dto.getReadStartDate() : LocalDate.now())
                .readStatus(dto.getReadStatus())
                .memo(dto.getMemo())
                .build();

        return historyConverter.convert(historyRepository.save(newHistory));
    }

    // 특정 회원의 독서 기록 목록을 조회
    @Transactional(readOnly = true)
    public List<ReadingHistoryResponseDto> getHistoriesByMember(String memberId) {
        return historyRepository.findHistoriesByMemberId(memberId).stream()
                .map(historyConverter::convert)
                .collect(Collectors.toList());
    }

    // 독서 기록을 삭제
    public void deleteHistory(Long historyId, String memberId) {
        ReadingHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 독서 기록 ID입니다."));

        // 기록 삭제 권한 검증
        if (!history.getMember().getMemberId().equals(memberId)) {
            throw new IllegalAccessError("해당 기록을 삭제할 권한이 없습니다.");
        }

        historyRepository.delete(history);
    }

    // 팔로잉하는 회원들의 독서 기록 목록을 조회
    @Transactional(readOnly = true)
    public List<ReadingHistoryResponseDto> getFollowingMembersHistory(String memberId) {
        // 팔로잉 ID 목록 조회
        List<String> followingIds = followService.getFollowingList(memberId);

        if (followingIds.isEmpty()) {
            return List.of();
        }

        // 팔로잉 ID들의 독서 기록 조회
        List<ReadingHistory> histories = historyRepository.findHistoriesByMemberIds(followingIds);

        return histories.stream()
                .map(historyConverter::convert)
                .collect(Collectors.toList());
    }

    // 특정 독서 기록의 상태를 업데이트
    public void updateReadingHistoryStatus(Long historyId, String memberId, ReadingHistoryStatusUpdateDto dto) {
        ReadingHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 독서 기록입니다."));

        // 기록 수정 권한 검증
        if (!history.getMember().getMemberId().equals(memberId)) {
            throw new IllegalAccessError("독서 기록을 수정할 권한이 없습니다.");
        }

        // 상태 업데이트
        try {
            history.setReadStatus(dto.getReadStatus());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 독서 상태 값입니다: " + dto.getReadStatus());
        }
    }
}