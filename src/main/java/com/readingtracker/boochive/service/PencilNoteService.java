package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.dto.*;
import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.mapper.PencilNoteMapper;
import com.readingtracker.boochive.repository.ReadingNoteRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PencilNoteService {

    private final ReadingNoteRepository readingNoteRepository;
    private final ResourceAccessUtil<ReadingNote> resourceAccessUtil;

    private final BookService bookService;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<PencilNoteResponse> findNoteById(Long id) {
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
    public Page<PencilNoteResponse> getNotesByUserAndNoteType(Long userId, Pageable pageable) {
        Page<ReadingNote> pageableNoteList = readingNoteRepository
                .findAllByUserIdAndNoteType(userId, NoteType.PENCIL, pageable);

        // 1. Page<Domain>를 List<Dto>로 변환
        List<PencilNoteResponse> noteList = pageableNoteList.getContent()
                .stream()
                .map(PencilNoteMapper.INSTANCE::toDto)
                .toList();

        // 2. ISBN을 기반으로 책 상세 정보 한 번에 조회
        List<String> isbnList = noteList.stream()
                .map(PencilNoteResponse::getBookIsbn)
                .distinct()
                .toList();
        Map<String, BookParameter> bookInfoMap = bookService.getBooksByIsbnList(isbnList)
                .stream()
                .collect(Collectors.toMap(BookParameter::getIsbn13, bookInfo -> bookInfo));

        // 3. 각 노트 레코드에 추가 정보 세팅
        noteList.forEach(note -> note.setBookInfo(bookInfoMap.get(note.getBookIsbn())));

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

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public PencilNoteResponse updatePencilNote(Long id, PencilNoteRequest pencilNote) {
        ReadingNote existingNote = resourceAccessUtil.checkAccessAndRetrieve(id);

        existingNote.updatePencilNote(pencilNote.getNoteText(), pencilNote.getCreateDate(), pencilNote.getIsPublic());

        return PencilNoteMapper.INSTANCE.toDto(existingNote);
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deletePencilNote(Long id) {
        ReadingNote existingNote = resourceAccessUtil.checkAccessAndRetrieve(id);

        readingNoteRepository.delete(existingNote);
    }
}
