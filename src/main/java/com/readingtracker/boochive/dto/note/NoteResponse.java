package com.readingtracker.boochive.dto.note;

import com.readingtracker.boochive.dto.book.BookDto;

public interface NoteResponse {

    String getBookIsbn();

    void setBookInfo(BookDto bookInfo);
}
