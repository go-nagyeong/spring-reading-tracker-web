package com.readingtracker.boochive.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.readingtracker.boochive.domain.QPurchaseHistory;
import com.readingtracker.boochive.domain.QReadingBook;
import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.reading.ReadingBookCondition;
import com.readingtracker.boochive.enums.ReadingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReadingBookDslRepositoryImpl implements ReadingBookDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReadingBook> findAllByUserAndFilters(User user, ReadingBookCondition condition, Pageable pageable) {
        // QClass
        QReadingBook readingBook = QReadingBook.readingBook;
        QPurchaseHistory purchaseHistory = QPurchaseHistory.purchaseHistory;

        // Base 쿼리 작성
        JPQLQuery<ReadingBook> query = queryFactory
                .selectFrom(readingBook)
                .where(
                        readingBook.user.eq(user),
                        equalsReadingStatus(readingBook, condition),
                        equalsCollectionId(readingBook, condition),
                        equalsOwn(purchaseHistory, condition)
                )
                .orderBy(readingBook.id.desc())
                .leftJoin(purchaseHistory)
                .on(
                        readingBook.user.eq(purchaseHistory.user),
                        readingBook.book.isbn13.eq(purchaseHistory.bookIsbn)
                );

        // 페이지네이션을 위한 Total Count 조회
        List<ReadingBook> list = query
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(list, pageable, totalCount);
    }

    private BooleanExpression equalsReadingStatus(QReadingBook readingBook, ReadingBookCondition condition) {
        if (condition.getReadingStatus() != null) {
            ReadingStatus readingStatus = ReadingStatus.valueOf(condition.getReadingStatus());
            return readingBook.readingStatus.eq(readingStatus);
        }
        return null;
    }

    private BooleanExpression equalsCollectionId(QReadingBook readingBook, ReadingBookCondition condition) {
        return condition.getCollectionId() != null
                ? readingBook.collection.id.eq(condition.getCollectionId())
                : null;
    }

    private BooleanExpression equalsOwn(QPurchaseHistory purchaseHistory, ReadingBookCondition condition) {
        return condition.getIsOwned() != null
                ? (condition.getIsOwned() ? purchaseHistory.id.isNotNull() : purchaseHistory.isNull())
                : null;
    }

}
