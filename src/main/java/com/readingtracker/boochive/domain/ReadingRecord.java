package com.readingtracker.boochive.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.readingtracker.boochive.util.CurrentUserContext;
import com.readingtracker.boochive.util.Own;
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
public class ReadingRecord implements Own {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

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
     * 신규 등록 전 값 자동 세팅
     */
    @PrePersist
    protected void prePersist() {
        // 현재 로그인 유저 정보 세팅
        this.user = CurrentUserContext.getUser();
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
