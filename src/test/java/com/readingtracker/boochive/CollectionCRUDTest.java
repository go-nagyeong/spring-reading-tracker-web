package com.readingtracker.boochive;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.CollectionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class CollectionCRUDTest {

    @Autowired
    private CollectionService service;

    @Test
    void createCollection() {
        /* given */
        BookCollection collection = BookCollection.builder()
                .userId(2L)
                .collectionName("test")
                .build();

        BookCollection savedCollection = service.saveCollection(collection);

        /* when, then */
        assertThat(savedCollection).isNotNull();
        assertThat(savedCollection.getUserId()).isEqualTo(2L);
        assertThat(savedCollection.getCollectionName()).isEqualTo("test");
    }

    @Test
    void updateCollection() {
        /* given */
        BookCollection collection = BookCollection.builder()
                .userId(2L)
                .collectionName("test")
                .build();

        BookCollection savedCollection = service.saveCollection(collection);

        BookCollection newCollection = BookCollection.builder()
                .id(savedCollection.getId())
                .build();
        newCollection.updateCollectionName("test 변경");
        BookCollection updatedCollection = service.saveCollection(newCollection);

        /* when, then */
        assertThat(updatedCollection).isNotNull();
        assertThat(updatedCollection.getUserId()).isEqualTo(2L);
        assertThat(updatedCollection.getCollectionName()).isEqualTo("test 변경");
    }
}
