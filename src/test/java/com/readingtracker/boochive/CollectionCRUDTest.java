package com.readingtracker.boochive;

import com.readingtracker.boochive.domain.BookCollection;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.mapper.UserMapper;
import com.readingtracker.boochive.service.CollectionService;
import com.readingtracker.boochive.service.UserService;
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
    @Autowired
    private UserService userService;

    @Test
    void createCollection() {
        /* given */
        User user = userService.findUserById(1L).map(UserMapper.INSTANCE::toEntity).get();

        BookCollection collection = BookCollection.builder()
                .user(user)
                .collectionName("test")
                .build();

        BookCollection savedCollection = service.createCollection(collection);

        /* when, then */
        assertThat(savedCollection).isNotNull();
        assertThat(savedCollection.getUser().getId()).isEqualTo(2L);
        assertThat(savedCollection.getCollectionName()).isEqualTo("test");
    }

    @Test
    void updateCollection() {
        /* given */
        User user = userService.findUserById(1L).map(UserMapper.INSTANCE::toEntity).get();

        BookCollection collection = BookCollection.builder()
                .user(user)
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
        assertThat(updatedCollection.getUser().getId()).isEqualTo(2L);
        assertThat(updatedCollection.getCollectionName()).isEqualTo("test 변경");
    }
}
