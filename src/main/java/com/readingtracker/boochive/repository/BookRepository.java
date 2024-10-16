package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn13(String isbn13);

    // 리뷰 개수, 평균 평점, 독자 수
    @Query(value = "SELECT COALESCE(rb.book_isbn, r.book_isbn) AS bookIsbn, " +
            "COUNT(DISTINCT r.rating) AS reviewCount, " +
            "AVG(CASE WHEN r.rating > 0 THEN r.rating END) AS averageRating, " +
            "COUNT(DISTINCT rb.id) AS readerCount " +
            "FROM ( " +
            "   SELECT b.isbn13, NULL AS book_isbn FROM book b WHERE b.isbn13 IN :isbnList " +
            "   UNION " +
            "   SELECT NULL AS isbn13, r.book_isbn FROM review r WHERE r.book_isbn IN :isbnList " +
            ") AS combined " +
            "LEFT JOIN reading_book rb ON combined.isbn13 = rb.book_isbn " +
            "LEFT JOIN review r ON combined.book_isbn = r.book_isbn " +
            "GROUP BY COALESCE(rb.book_isbn, r.book_isbn)", nativeQuery = true)
    List<Object[]> getBookStatistics(List<String> isbnList);
}
