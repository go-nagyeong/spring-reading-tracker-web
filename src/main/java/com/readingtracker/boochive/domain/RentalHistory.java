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
@Table(name = "book_rental_history", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_isbn"})
})
public class RentalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 20, nullable = false)
    private String bookIsbn;

    private LocalDate rentalDate;

    private LocalDate returnDate;

    private String rentalFrom;

    private String memo;

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
     * 구매 이력 정보 변경
     */
    public void updateHistory(LocalDate rentalDate, LocalDate returnDate, String rentalFrom, String memo) {
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.rentalFrom = rentalFrom;
        this.memo = memo;
    }
}
