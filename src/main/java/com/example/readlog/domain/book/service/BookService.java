package com.example.readlog.domain.book.service;

import com.example.readlog.domain.book.converter.BookConverter;
import com.example.readlog.domain.book.dto.BookSearchDto;
import com.example.readlog.domain.book.entity.Book;
import com.example.readlog.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 책 검색 비즈니스 로직을 처리하는 서비스
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookConverter bookConverter;

    // 제목 키워드로 책 목록을 검색
    public List<BookSearchDto> searchBooksByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        List<Book> books = bookRepository.searchByTitlePrefix(keyword);

        return books.stream()
                .map(bookConverter::convert)
                .collect(Collectors.toList());
    }

    // 저자 키워드로 책 목록을 검색
    public List<BookSearchDto> searchBooksByAuthor(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        List<Book> books = bookRepository.searchByAuthorPrefix(keyword);

        return books.stream()
                .map(bookConverter::convert)
                .collect(Collectors.toList());
    }
}