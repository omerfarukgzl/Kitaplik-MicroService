package com.kitaplik.bookservice.service;

import com.kitaplik.bookservice.dto.BookDto;
import com.kitaplik.bookservice.dto.BookIdDto;
import com.kitaplik.bookservice.dto.CreateBookRequest;
import com.kitaplik.bookservice.dto.converter.BookDtoConverter;
import com.kitaplik.bookservice.exception.BookNotFoundExcepiton;
import com.kitaplik.bookservice.model.Book;
import com.kitaplik.bookservice.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
   private final BookDtoConverter bookDtoConverter;

    public BookService(BookRepository bookRepository ,BookDtoConverter bookDtoConverter) {
        this.bookRepository = bookRepository;
        this.bookDtoConverter = bookDtoConverter;
    }


    public List<BookDto> getAllBooks()
    {
        return bookRepository.findAll()
                .stream()
                .map(bookDtoConverter::convert)
                .collect(Collectors.toList());
    }

    public BookDto createBook(@Valid CreateBookRequest createBookRequest)
    {
        Book book= new Book("",
                createBookRequest.getTitle(),
                createBookRequest.getBookYear(),
                createBookRequest.getAuthor(),
                createBookRequest.getPressName(),
                createBookRequest.getIsbn());
        BookDto bookDto = bookDtoConverter.convert(bookRepository.save(book));
        return bookDto;

    }
    
    public BookIdDto findByIsbn(String isbn)
    {
     return bookRepository.findBookByIsbn(isbn)
             .map(book -> new BookIdDto(book.getId(),book.getIsbn()))
             .orElseThrow(() -> new BookNotFoundExcepiton("Book could not found by isbn: " + isbn));
    }

    public BookDto findBookDetailsById(String id)
    {
        return bookRepository.findById(id)
                .map(bookDtoConverter::convert)
                .orElseThrow(() -> new BookNotFoundExcepiton("Book could not found by id: " + id));
    }



}
