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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

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
     */
    @PrePersist
    protected void prePersist() {
        // 현재 로그인 유저 정보 세팅
        this.user = CurrentUserContext.getUser();
        // 컬렉션으로 신규 등록시 독서 상태 기본값 자동 세팅 (기본값: 읽을 예정)
        this.readingStatus = this.readingStatus == null ? ReadingStatus.TO_READ : this.readingStatus;
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
