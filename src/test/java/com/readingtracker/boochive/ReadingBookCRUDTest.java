package com.readingtracker.boochive;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingStatus;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.service.ReadingBookService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class ReadingBookCRUDTest {

    @Autowired
    private ReadingBookService service;
    @Autowired
    private CollectionService collectionService;

    @Test
    void createByReadingStatus() {
        /* given */
        ReadingBook readingBook = ReadingBook.builder()
                .userId(1L)
                .bookIsbn("test")
                .readingStatus(ReadingStatus.READING)
                .build();

        ReadingBook savedReadingBook = service.createReadingBook(readingBook);

        /* when, then */
        assertThat(savedReadingBook).isNotNull();
        assertThat(savedReadingBook.getUserId()).isEqualTo(1L);
        assertThat(savedReadingBook.getBookIsbn()).isEqualTo("test");
        assertThat(savedReadingBook.getReadingStatus()).isEqualTo(ReadingStatus.READING);
    }

    @Test
    void createByCollection() {
        /* given */
        BookCollection collection = BookCollection.builder()
                .userId(1L)
                .collectionName("test")
                .build();

        BookCollection createdCollection = collectionService.createCollection(collection);

        ReadingBook readingBook = ReadingBook.builder()
                .userId(1L)
                .bookIsbn("test")
                .collectionId(createdCollection.getId())
                .build();

        ReadingBook savedReadingBook = service.createReadingBook(readingBook);

        /* when, then */
        assertThat(savedReadingBook).isNotNull();
        assertThat(savedReadingBook.getUserId()).isEqualTo(1L);
        assertThat(savedReadingBook.getBookIsbn()).isEqualTo("test");
        assertThat(savedReadingBook.getReadingStatus()).isEqualTo(ReadingStatus.TO_READ);
        assertThat(savedReadingBook.getCollectionId()).isEqualTo(createdCollection.getId());
    }

    @Test
    void updateReadingStatus() {
        /* given */
        BookCollection collection = BookCollection.builder()
                .userId(1L)
                .collectionName("test")
                .build();

        BookCollection createdCollection = collectionService.createCollection(collection);

        ReadingBook readingBook = ReadingBook.builder()
                .userId(1L)
                .bookIsbn("test")
                .readingStatus(ReadingStatus.READING)
                .collectionId(createdCollection.getId())
                .build();

        ReadingBook createdReadingBook = service.createReadingBook(readingBook);

        ReadingBook newReadingBook = ReadingBook.builder()
                .id(createdReadingBook.getId())
                .build();
        newReadingBook.updateReadingStatus(ReadingStatus.PAUSED);
        newReadingBook.updateCollectionId(readingBook.getCollectionId()); // 수정 필드가 아니더라도 기존 값 넣어줘야 함
        ReadingBook updatedReadingBook = service.updateReadingBook(createdReadingBook.getId(), newReadingBook);

        /* when, then */
        assertThat(updatedReadingBook).isNotNull();
        assertThat(updatedReadingBook.getUserId()).isEqualTo(1L);
        assertThat(updatedReadingBook.getBookIsbn()).isEqualTo("test");
        assertThat(updatedReadingBook.getReadingStatus()).isEqualTo(ReadingStatus.PAUSED);
        assertThat(updatedReadingBook.getCollectionId()).isEqualTo(createdCollection.getId());
    }

    @Test
    void updateCollection() {
        /* given */
        BookCollection collection = BookCollection.builder()
                .userId(1L)
                .collectionName("test")
                .build();

        BookCollection createdCollection = collectionService.createCollection(collection);

        ReadingBook readingBook = ReadingBook.builder()
                .userId(1L)
                .bookIsbn("test")
                .readingStatus(ReadingStatus.READING)
                .collectionId(createdCollection.getId())
                .build();

        ReadingBook createdReadingBook = service.createReadingBook(readingBook);

        ReadingBook newReadingBook = ReadingBook.builder()
                .id(createdReadingBook.getId())
                .build();
        newReadingBook.updateReadingStatus(readingBook.getReadingStatus()); // 수정 필드가 아니더라도 기존 값 넣어줘야 함
        newReadingBook.updateCollectionId(createdCollection.getId());
        ReadingBook updatedReadingBook = service.updateReadingBook(createdReadingBook.getId(), newReadingBook);

        /* when, then */
        assertThat(updatedReadingBook).isNotNull();
        assertThat(updatedReadingBook.getUserId()).isEqualTo(1L);
        assertThat(updatedReadingBook.getBookIsbn()).isEqualTo("test");
        assertThat(updatedReadingBook.getReadingStatus()).isEqualTo(ReadingStatus.READING);
        assertThat(updatedReadingBook.getCollectionId()).isEqualTo(createdCollection.getId());
    }

}
