package com.readingtracker.boochive;

import com.readingtracker.boochive.domain.BookCollection;
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

        BookCollection savedCollection = service.createCollection(collection);

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

        BookCollection createdCollection = service.createCollection(collection);

        BookCollection newCollection = BookCollection.builder()
                .id(createdCollection.getId())
                .build();
        newCollection.updateCollectionName("test 변경");
        BookCollection updatedCollection = service.updateCollection(createdCollection.getId(), newCollection);

        /* when, then */
        assertThat(updatedCollection).isNotNull();
        assertThat(updatedCollection.getUserId()).isEqualTo(2L);
        assertThat(updatedCollection.getCollectionName()).isEqualTo("test 변경");
    }
}
