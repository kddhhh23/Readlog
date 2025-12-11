package com.example.readlog.domain.book.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Builder
public class BookSearchDto {
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
}
