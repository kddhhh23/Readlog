package com.example.readlog.domain.book.converter;

import com.example.readlog.domain.book.dto.BookSearchDto;
import com.example.readlog.domain.book.entity.Book;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// Book 엔티티를 BookSearchDto로 변환하는 컨버터
@Component
public class BookConverter implements Converter<Book, BookSearchDto> {

    // Book 엔티티를 DTO로 변환
    @Override
    public BookSearchDto convert(Book source) {
        if (source == null) {
            return null;
        }

        return BookSearchDto.builder()
                .bookId(source.getBookId())
                .title(source.getTitle())
                .author(source.getAuthor())
                .publisher(source.getPublisher())
                .publishDate(source.getPublishDate())
                .build();
    }
}