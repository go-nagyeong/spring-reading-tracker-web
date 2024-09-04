package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.*;
import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.mapper.HighlightNoteMapper;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingNoteService {

    private final ReadingNoteRepository readingNoteRepository;
    private final ResourceAccessUtil<ReadingNote> resourceAccessUtil;
    private final FileStorageUtil fileStorageUtil;

    private final BookService bookService;

    private final String uploadDir = "images/notes/";

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<PencilNoteResponse> findPencilNoteById(Long id) {

        return readingNoteRepository.findById(id)
                .map(note -> {
                    resourceAccessUtil.checkAccess(note); // 데이터 접근 권한 검사
                    PencilNoteResponse dto = PencilNoteMapper.INSTANCE.toDto(note);
                    bookService.findBookByIsbn(note.getBookIsbn()) // 책 상세 정보 세팅
                            .ifPresent(dto::setBookInfo);
                    return dto;
                });
    }

    @Transactional(readOnly = true)
    public Optional<HighlightNoteResponse> findHighlightNoteById(Long id) {

        return readingNoteRepository.findById(id)
                .map(note -> {
                    resourceAccessUtil.checkAccess(note); // 데이터 접근 권한 검사
                    HighlightNoteResponse dto = HighlightNoteMapper.INSTANCE.toDto(note);
                    bookService.findBookByIsbn(note.getBookIsbn()) // 책 상세 정보 세팅
                            .ifPresent(dto::setBookInfo);
                    return dto;
                });
    }

    @Transactional(readOnly = true)
    public Page<PencilNoteResponse> getPencilNotesByUserAndNoteType(Long userId, Pageable pageable) {
        Page<ReadingNote> pageableNoteList = readingNoteRepository
                .findAllByUserIdAndNoteTypeOrderByIdDesc(userId, NoteType.PENCIL, pageable);

        // Page<Domain>를 List<Dto>로 변환
        List<PencilNoteResponse> noteList = pageableNoteList.getContent()
                .stream()
                .map(PencilNoteMapper.INSTANCE::toDto)
                .toList();

        // DTO에 책 상세 정보 세텡
        setBookInfoEachRecord(noteList);

        return new PageImpl<>(noteList, pageable, pageableNoteList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<HighlightNoteResponse> getHighlightNotesByUserAndNoteType(Long userId, Pageable pageable) {
        Page<ReadingNote> pageableNoteList = readingNoteRepository
                .findAllByUserIdAndNoteTypeOrderByIdDesc(userId, NoteType.HIGHLIGHT, pageable);

        // Page<Domain>를 List<Dto>로 변환
        List<HighlightNoteResponse> noteList = pageableNoteList.getContent()
                .stream()
                .map(HighlightNoteMapper.INSTANCE::toDto)
                .toList();

        // DTO에 책 상세 정보 세텡
        setBookInfoEachRecord(noteList);

        return new PageImpl<>(noteList, pageable, pageableNoteList.getTotalElements());
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public PencilNoteResponse createPencilNote(PencilNoteRequest pencilNote) {
        ReadingNote newNote = PencilNoteMapper.INSTANCE.toEntity(pencilNote);

        return PencilNoteMapper.INSTANCE.toDto(readingNoteRepository.save(newNote));
    }

    @Transactional
    public HighlightNoteResponse createHighlightNote(HighlightNoteRequest highlightNote) throws IOException {
        ReadingNote newNote = HighlightNoteMapper.INSTANCE.toEntity(highlightNote);

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
                highlightNote.getHighlightColor(), highlightNote.getPageNumber()
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
     * (공통 메서드) 책 상세 정보 데이터 세팅 (DTO 데이터 전처리)
     */
    private void setBookInfoEachRecord(List<? extends NoteResponse> noteList) {
        // 2. ISBN을 기반으로 책 상세 정보 한 번에 조회
        List<String> isbnList = noteList.stream()
                .map(NoteResponse::getBookIsbn)
                .distinct()
                .toList();
        Map<String, BookDto> bookInfoMap = bookService.getBooksByIsbnList(isbnList)
                .stream()
                .collect(Collectors.toMap(BookDto::getIsbn13, bookInfo -> bookInfo));

        // 3. 각 노트 레코드에 추가 정보 세팅
        noteList.forEach(note -> note.setBookInfo(bookInfoMap.get(note.getBookIsbn())));
    }
}
