package com.kitaplik.bookservice.controller;

import com.kitaplik.bookservice.dto.BookDto;
import com.kitaplik.bookservice.dto.BookIdDto;
import com.kitaplik.bookservice.dto.CreateBookRequest;
import com.kitaplik.bookservice.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/book")
    public ResponseEntity<BookDto> createBook(@RequestBody CreateBookRequest bookRequest)
    {
        BookDto bookDto1 = bookService.createBook(bookRequest);
        return ResponseEntity.ok(bookDto1);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable @NotEmpty String id)
    {
        BookDto bookDto = bookService.findBookDetailsById(id);
        return ResponseEntity.ok(bookDto);
    }
}
