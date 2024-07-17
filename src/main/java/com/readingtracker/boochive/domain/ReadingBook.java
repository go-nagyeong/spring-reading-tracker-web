package com.readingtracker.boochive.domain;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_isbn"})
})
@EntityListeners(AuditingEntityListener.class)
public class ReadingBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 20, nullable = false)
    private String bookIsbn;

    private Long collectionId;

    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 신규 등록 전 값 자동 세팅
     * -> 컬렉션으로 신규 등록시 독서 상태 기본값 자동 세팅 (기본값: 읽을 예정)
     */
    @PrePersist
    protected void prePersist() {
        this.readingStatus = this.readingStatus == null ? ReadingStatus.TO_READ : this.readingStatus;
    }

    /**
     * 사용자 변경
     */
    public void updateUserId(Long userId) {
        this.userId = userId;
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
    public void updateCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }
}
