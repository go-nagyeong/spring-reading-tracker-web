package com.readingtracker.boochive.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "collection")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class BookCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String collectionName;

    @Column
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
