package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.common.BatchOperationRequest;
import com.readingtracker.boochive.dto.note.*;
import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.enums.ResourceName;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import com.readingtracker.boochive.mapper.*;
import com.readingtracker.boochive.repository.ReadingBookJpaRepository;
import com.readingtracker.boochive.repository.ReadingNoteRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostItService {

    private final ReadingNoteRepository readingNoteRepository;
    private final ReadingBookJpaRepository readingBookRepository;

    private final ResourceAccessUtil<ReadingNote> resourceAccessUtil;
    private final ResourceAccessUtil<ReadingBook> readingBookResourceAccessUtil;

    private final ResourceName readingBookResourceName = ResourceName.fromClassName(ReadingBook.class.getSimpleName());

    // 도서 당 포스트잇 최대 등록 개수 (NOTE: UI 구조를 감안하여 개수 제한을 두게 되었음)
    private static final int MAX_POST_IT_COUNT = 30;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Page<BookWithPostItsResponse> findBooksWithPostItsByUser(Long userId, Pageable pageable) {
        // 1. 책 조회
        Page<ReadingBook> pageableBookList = readingBookRepository
                .findAllByUserIdAndNoteTypeOrderByNoteIdDesc(userId, NoteType.POST_IT, pageable);

        // 2. DTO 변환 및 관계 데이터 세팅
        List<BookWithPostItsResponse> bookListWithPostIts = pageableBookList.getContent()
                .stream()
                .map(this::setBookWithPostItsResponse)
                .toList();

        return new PageImpl<>(bookListWithPostIts, pageable, pageableBookList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public BookWithPostItsResponse getBookWithPostIts(Long readingBookId) {
        return readingBookRepository
                .findByIdWithReadingNotes(readingBookId, NoteType.POST_IT)
                .map(this::setBookWithPostItsResponse)
                .orElseThrow(() -> new ResourceNotFoundException(readingBookResourceName.getName())); // 데이터 유무 검사
    }

    /**
     * [C]R[U][D] - BATCH CREATE/UPDATE/DELETE
     */
    @Transactional
    public void handlePostIts(BatchOperationRequest<PostItRequest> request) {
        Set<Long> readingBookIdSet = new HashSet<>(); // 참조 ID 예외 처리를 위해 (Unique 적용을 위해 HashSet 사용)

        for (PostItRequest postIt : request.getCreateList()) {
            ReadingNote newPostIt = PostItMapper.INSTANCE.toEntity(postIt);
            readingNoteRepository.save(newPostIt);

            readingBookIdSet.add(newPostIt.getReadingBook().getId());
        }

        for (PostItRequest postIt : request.getUpdateList()) {
            ReadingNote existingPostIt = resourceAccessUtil.checkAccessAndRetrieve(postIt.getId());
            existingPostIt.updatePostIt(postIt.getNoteText(), postIt.getBackgroundColor(), postIt.getPageNumber());

            readingBookIdSet.add(existingPostIt.getReadingBook().getId());
        }

        for (Long id : request.getDeleteList()) {
            ReadingNote existingPostIt = resourceAccessUtil.checkAccessAndRetrieve(id);
            readingBookIdSet.add(existingPostIt.getReadingBook().getId());
            readingNoteRepository.delete(existingPostIt);
        }

        if (readingBookIdSet.size() > 1) {
            throw new IllegalArgumentException("책 정보가 일치하지 않습니다. 문제가 계속되면 관리자에게 문의해 주세요");
        }
        Long readingBookId = readingBookIdSet.iterator().next();

        // 저장 전 참조 데이터(독서 책) 유효성 검증
        validateReferenceReadingBook(readingBookId);

        // 포스트잇 등록 개수 제한 검증
        long currentPostItCount = readingNoteRepository.countByReadingBookIdAndNoteType(readingBookId, NoteType.POST_IT);
        if (currentPostItCount > MAX_POST_IT_COUNT) {
            throw new IllegalArgumentException("포스트잇은 최대 " + MAX_POST_IT_COUNT + "개까지 등록하실 수 있습니다.");
        }
    }

    /**
     * (공통 메서드) DTO 변환 및 관련 데이터 세팅 (ReadingBook 기준 데이터에 포스트잇 목록 세팅)
     */
    private BookWithPostItsResponse setBookWithPostItsResponse(ReadingBook readingBook) {
        // 데이터 접근 권한 검사
        readingBookResourceAccessUtil.checkAccess(readingBook);

        // DTO 변환
        BookWithPostItsResponse bookWithPostItsResponse = BookWithPostItsMapper.INSTANCE.toDto(readingBook);

        // 포스트잇 목록 세팅
        // (NOTE: NoteType 조건 때문에 컬렉션처럼 별도 조회 방식을 안 쓰고 Fetch Join 방식을 씀)
        bookWithPostItsResponse.setPostIts(
                readingBook.getReadingNotes()
                        .stream()
                        .map(PostItMapper.INSTANCE::toDto)
                        .sorted(Comparator
                                .comparing(PostItResponse::getPageNumber, Comparator.nullsFirst(Comparator.naturalOrder()))
                                .thenComparing(PostItResponse::getId))
                        .toList()
        );

        return bookWithPostItsResponse;
    }

    /**
     * (공통 메서드) 참조 데이터 유효성 검증 - 독서 책
     */
    private void validateReferenceReadingBook(Long readingBookId) {
        readingBookResourceAccessUtil.checkAccessAndRetrieve(readingBookId);
    }
}