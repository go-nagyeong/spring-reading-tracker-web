package com.readingtracker.boochive.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.readingtracker.boochive.enums.NoteType;
import com.readingtracker.boochive.util.CurrentUserContext;
import com.readingtracker.boochive.util.Own;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
public class ReadingNote implements Own {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column(length = 20, nullable = false)
    private String bookIsbn;

    @Enumerated(EnumType.STRING)
    private NoteType noteType;

    private String noteText;

    private String attachmentImage;

    @ColumnDefault("0")
    private Integer isPublic; // 0: 비공개, 1: 전체 공개

    private Integer pageNumber;

    private String highlightColor; // [형광펜]노트의 하이라이트 색상 or [포스트잇]노트의 포스트잇 색상

    private LocalDate createDate; // 사용자가 변경할 수 있는 작성일자

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
     * [연필]노트 수정
     */
    public void updatePencilNote(String noteText, LocalDate createDate, Integer isPublic) {
        this.noteText = noteText;
        this.createDate = createDate;
        this.isPublic = isPublic;
    }

    /**
     * [형광펜]노트 수정
     */
    public void updateHighlightNote(String noteText, String attachmentImage, String highlightColor, Integer pageNumber) {
        this.noteText = noteText;
        this.attachmentImage = attachmentImage;
        this.highlightColor = highlightColor;
        this.pageNumber = pageNumber;
    }

    /**
     * [포스트잇]노트 수정
     */
    public void updatePostItNote(String noteText, String highlightColor, Integer pageNumber) {
        this.noteText = noteText;
        this.highlightColor = highlightColor;
        this.pageNumber = pageNumber;
    }

    /**
     * 노트 작성일자 및 공개여부 수정
     */
    public void updateCreateDateAndIsPublic(LocalDate createDate, Integer isPublic) {
        this.createDate = createDate;
        this.isPublic = isPublic;
    }
}
