package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {

    Optional<ReadingRecord> findByUserIdAndBookIsbnAndStartDate(Long userId, String bookIsbn, LocalDate startDate);

    Optional<ReadingRecord> findFirstByUserIdAndBookIsbnAndEndDateIsNullOrderByIdDesc(Long userId, String bookIsbn);

    Optional<ReadingRecord> findFirstByUserIdAndBookIsbnAndEndDateIsNotNullOrderByIdDesc(Long userId, String bookIsbn);

    List<ReadingRecord> findAllByUserIdAndBookIsbn(Long userId, String bookIsbn);

    Integer countByUserIdAndBookIsbnAndEndDateIsNotNull(Long userId, String bookIsbn);
}
