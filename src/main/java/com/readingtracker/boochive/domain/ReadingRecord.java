package com.readingtracker.boochive.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReadingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 20, nullable = false)
    private String bookIsbn;

    private LocalDate startDate;

    private LocalDate endDate;

    @Transient
    private Long dDay; // 클라이언트에 전달할 D-Day 계산 결과

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 사용자 변경
     */
    public void updateUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 독서 시작일 변경
     */
    public void updateStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * 완독일 변경
     */
    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * 독서 디데이 변경
     */
    public void updateDDay(Long dDay) {
        this.dDay = dDay;
    }
}
