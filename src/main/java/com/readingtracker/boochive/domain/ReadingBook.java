package com.readingtracker.boochive.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.readingtracker.boochive.enums.ReadingStatus;
import com.readingtracker.boochive.util.CurrentUserContext;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_isbn"})
})
@EntityListeners(AuditingEntityListener.class)
public class ReadingBook implements Own {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private BookCollection collection;

    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "readingBook", cascade = CascadeType.REMOVE)
    private List<ReadingNote> readingNotes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "readingBook", cascade = CascadeType.REMOVE)
    private List<ReadingRecord> readingRecords = new ArrayList<>();

    /**
     * 신규 등록 전 값 자동 세팅
     */
    @PrePersist
    protected void prePersist() {
        // 현재 로그인 유저 정보 세팅
        this.user = CurrentUserContext.getUser();
    }

    /**
     * 독서 상태 변경
     */
    public void updateReadingStatus(ReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    /**
     * 컬렉션 변경
     */
    public void updateCollection(BookCollection collection) {
        this.collection = collection;
    }
}
