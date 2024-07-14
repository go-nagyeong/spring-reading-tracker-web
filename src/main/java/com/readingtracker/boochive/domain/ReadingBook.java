package com.readingtracker.boochive.domain;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reading_list")
@SQLDelete(sql = "UPDATE reading_list SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class ReadingBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String bookIsbn;

    @Column
    private Long collectionId;

    @Enumerated(EnumType.STRING)
    @Column
    private ReadingStatus readingStatus;

    @Column
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
