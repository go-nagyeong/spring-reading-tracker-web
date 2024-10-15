package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingBook;
import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.note.*;
import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.mapper.BookMapper;
import com.readingtracker.boochive.mapper.HighlightNoteMapper;
import com.readingtracker.boochive.mapper.NoteMapper;
import com.readingtracker.boochive.mapper.PencilNoteMapper;
import com.readingtracker.boochive.repository.ReadingNoteRepository;
import com.readingtracker.boochive.util.FileStorageUtil;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingNoteService {

    private final ReadingNoteRepository readingNoteRepository;
    private final ResourceAccessUtil<ReadingNote> resourceAccessUtil;
    private final ResourceAccessUtil<ReadingBook> readingBookResourceAccessUtil;

    private final FileStorageUtil fileStorageUtil;

    private final String uploadDir = "images/notes/";

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<PencilNoteResponse> findPencilNoteById(Long id) {
        return findNoteById(id, PencilNoteMapper.INSTANCE);
    }

    @Transactional(readOnly = true)
    public Optional<HighlightNoteResponse> findHighlightNoteById(Long id) {
        return findNoteById(id, HighlightNoteMapper.INSTANCE);
    }

    @Transactional(readOnly = true)
    public Page<PencilNoteResponse> getPencilNotesByUser(Long userId, Pageable pageable) {
        return getNotesByUserAndNoteType(userId, NoteType.PENCIL, PencilNoteMapper.INSTANCE, pageable);
    }

    @Transactional(readOnly = true)
    public Page<HighlightNoteResponse> getHighlightNotesByUser(Long userId, Pageable pageable) {
        return getNotesByUserAndNoteType(userId, NoteType.HIGHLIGHT, HighlightNoteMapper.INSTANCE, pageable);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public PencilNoteResponse createPencilNote(PencilNoteRequest pencilNote) {
        ReadingNote newNote = PencilNoteMapper.INSTANCE.toEntity(pencilNote);

        // 저장 전 참조 데이터(독서 책) 유효성 검증
        validateReferenceReadingBook(pencilNote.getReadingBookId());

        return PencilNoteMapper.INSTANCE.toDto(readingNoteRepository.save(newNote));
    }

    @Transactional
    public HighlightNoteResponse createHighlightNote(HighlightNoteRequest highlightNote) throws IOException {
        ReadingNote newNote = HighlightNoteMapper.INSTANCE.toEntity(highlightNote);

        // 저장 전 참조 데이터(독서 책) 유효성 검증
        validateReferenceReadingBook(highlightNote.getReadingBookId());

        ReadingNote savedNote = readingNoteRepository.save(newNote);

        // 노트 신규 생성 후 첨부 파일 이동
        String tempFileUrl = savedNote.getAttachmentImage();
        if (tempFileUrl != null) {
            String finalDirectory = uploadDir + savedNote.getId();

            try {
                // 파일 이동 시도
                String finalFileUrl = fileStorageUtil.moveFile(tempFileUrl, finalDirectory); // 파일 이동

                // 최종 URL 업데이트
                savedNote.updateAttachmentImageUrl(finalFileUrl);

            } catch (IOException e) {
                // 파일 이동 실패 시 예외 처리 (DB 작업 수동으로 되돌리기)
                fileStorageUtil.deleteFile(tempFileUrl);
                fileStorageUtil.deleteDirectory(finalDirectory);
                readingNoteRepository.delete(savedNote);

                throw new IOException(e);
            }
        }

        return HighlightNoteMapper.INSTANCE.toDto(readingNoteRepository.save(savedNote));
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public PencilNoteResponse updatePencilNote(Long id, PencilNoteRequest pencilNote) {
        ReadingNote existingNote = resourceAccessUtil.checkAccessAndRetrieve(id);

        existingNote.updatePencilNote(pencilNote.getNoteText());
        existingNote.updateCreateDateAndIsPublic(pencilNote.getCreateDate(), pencilNote.getIsPublic());

        return PencilNoteMapper.INSTANCE.toDto(existingNote);
    }

    @Transactional
    public HighlightNoteResponse updateHighlightNote(Long id, HighlightNoteRequest highlightNote) {
        ReadingNote existingNote = resourceAccessUtil.checkAccessAndRetrieve(id);

        existingNote.updateHighlightNote(
                highlightNote.getNoteText(), highlightNote.getAttachmentImage(),
                highlightNote.getBackgroundColor(), highlightNote.getPageNumber()
        );
        existingNote.updateCreateDateAndIsPublic(highlightNote.getCreateDate(), highlightNote.getIsPublic());

        return HighlightNoteMapper.INSTANCE.toDto(existingNote);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteNoteById(Long id) throws IOException {
        ReadingNote existingNote = resourceAccessUtil.checkAccessAndRetrieve(id);

        // 첨부 파일도 삭제
        String filePath = uploadDir + id;
        fileStorageUtil.deleteDirectory(filePath);

        readingNoteRepository.delete(existingNote);
    }

    /**
     * (공통 메서드) READ 공통 로직 1
     */
    private <T extends NoteResponse, M extends NoteMapper> Optional<T> findNoteById(Long id, M dtoMapper) {
        return readingNoteRepository.findById(id)
                .map(note -> {
                    // 데이터 접근 권한 검사
                    resourceAccessUtil.checkAccess(note);
                    // DTO 변환
                    T response = dtoMapper.toDto(note);
                    // 추가 정보 1. 책 상세 정보
                    response.setBookInfo(BookMapper.INSTANCE.toDto(note.getReadingBook().getBook()));
                    return response;
                });
    }

    /**
     * (공통 메서드) READ 공통 로직 2
     */
    private <T extends NoteResponse, M extends NoteMapper> Page<T> getNotesByUserAndNoteType(
            Long userId, NoteType noteType, M dtoMapper, Pageable pageable
    ) {
        Page<ReadingNote> pageableNoteList = readingNoteRepository
                .findAllByUserIdAndNoteTypeOrderByIdDesc(userId, noteType, pageable);

        // DTO 변환 및 관계 데이터 세팅
        List<T> noteList = pageableNoteList.getContent()
                .stream()
                .map(note -> {
                    T response = dtoMapper.toDto(note);
                    // 추가 정보 1. 책 상세 정보
                    response.setBookInfo(BookMapper.INSTANCE.toDto(note.getReadingBook().getBook()));
                    return response;
                })
                .toList();

        return new PageImpl<>(noteList, pageable, pageableNoteList.getTotalElements());
    }

    /**
     * (공통 메서드) 참조 데이터 유효성 검증 - 독서 책
     */
    private void validateReferenceReadingBook(Long readingBookId) {
        readingBookResourceAccessUtil.checkAccessAndRetrieve(readingBookId);
    }
}
