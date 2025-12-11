package com.example.readlog.domain.book.controller;

import com.example.readlog.domain.book.dto.BookSearchDto;
import com.example.readlog.domain.book.service.BookService;
import com.example.readlog.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 책 검색 및 관련 API를 처리하는 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 제목 또는 저자로 책을 검색하고 결과를 반환
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchBooks(
            @RequestParam(required = true) String type,
            @RequestParam(required = false) String keyword) {

        List<BookSearchDto> results;

        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.success("검색 키워드가 없습니다. 빈 목록을 반환합니다.", List.of())
            );
        }

        // 검색 유형에 따라 책을 조회
        results = switch (type.toLowerCase()) {
            case "title" -> bookService.searchBooksByTitle(keyword);
            case "author" -> bookService.searchBooksByAuthor(keyword);
            default -> throw new IllegalArgumentException("잘못된 검색 유형입니다. (title 또는 author를 사용하세요)");
        };

        String message = results.isEmpty() ? "검색 결과가 없습니다." : "총 " + results.size() + "건의 결과를 조회했습니다.";

        return ResponseEntity.ok(
                ApiResponse.success(message, results)
        );
    }
}