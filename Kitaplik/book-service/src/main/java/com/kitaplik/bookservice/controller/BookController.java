package com.kitaplik.bookservice.controller;

import com.kitaplik.bookservice.dto.BookDto;
import com.kitaplik.bookservice.dto.BookIdDto;
import com.kitaplik.bookservice.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/v1/book")
@Validated
public class BookController {
    private  final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping()
    public ResponseEntity<List<BookDto>> getAllBook()
    {
       List<BookDto> bookDto = bookService.getAllBooks();
        return ResponseEntity.ok(bookDto);
    }
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookIdDto> getBookByIsbn(@PathVariable @NotEmpty String isbn)
    {
        BookIdDto bookIdDto = bookService.findByIsbn(isbn);
        return ResponseEntity.ok(bookIdDto);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<BookDto> getBookFindById(@PathVariable @NotEmpty String id)
    {
        BookDto bookDto = bookService.findById(id);
        return ResponseEntity.ok(bookDto);
    }
}
