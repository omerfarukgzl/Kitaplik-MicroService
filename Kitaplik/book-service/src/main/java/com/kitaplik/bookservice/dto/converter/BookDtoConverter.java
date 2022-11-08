package com.kitaplik.bookservice.dto.converter;

import com.kitaplik.bookservice.dto.BookDto;
import com.kitaplik.bookservice.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookDtoConverter {
    private final BookIdDtoConverter bookIdDtoConverter;

    public BookDtoConverter(BookIdDtoConverter bookIdDtoConverter) {
        this.bookIdDtoConverter = bookIdDtoConverter;
    }

    public BookDto convert(Book from) {
        return new BookDto(
                bookIdDtoConverter.convert(from.getId(),from.getIsbn()),
                from.getTitle(),
                from.getBookYear(),
                from.getAuthor(),
                from.getPressName());
    }
}
