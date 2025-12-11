package com.example.readlog.domain.book.repository;

import com.example.readlog.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 제목으로 책을 Prefix 검색, 인덱스 사용
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT(:titleKeyword, '%'))")
    List<Book> searchByTitlePrefix(@Param("titleKeyword") String titleKeyword);

    // 저자명으로 책을 Prefix 검색, 인덱스 사용
    @Query("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT(:authorKeyword, '%'))")
    List<Book> searchByAuthorPrefix(@Param("authorKeyword") String authorKeyword);
}