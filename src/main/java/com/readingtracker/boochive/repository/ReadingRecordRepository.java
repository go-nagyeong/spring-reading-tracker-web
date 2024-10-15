package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {

    Optional<ReadingRecord> findByUserIdAndReadingBookIdAndStartDate(Long userId, Long readingBookId, LocalDate startDate);

    Optional<ReadingRecord> findFirstByUserIdAndReadingBookIdAndEndDateIsNullOrderByIdDesc(Long userId, Long readingBookId);

    Optional<ReadingRecord> findFirstByUserIdAndReadingBookIdAndEndDateIsNotNullOrderByIdDesc(Long userId, Long readingBookId);

    List<ReadingRecord> findAllByUserIdAndReadingBookId(Long userId, Long readingBookId);

    Integer countByUserIdAndReadingBookIdAndEndDateIsNotNull(Long userId, Long readingBookId);
}
