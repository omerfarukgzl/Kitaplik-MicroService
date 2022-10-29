package com.kitaplik.bookservice.service;

import com.kitaplik.bookservice.dto.BookDto;
import com.kitaplik.bookservice.dto.BookIdDto;
import com.kitaplik.bookservice.exception.BookNotFoundExcepiton;
import com.kitaplik.bookservice.model.Book;
import com.kitaplik.bookservice.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookService(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }


    public List<BookDto> getAllBooks()
    {
        List<Book> books = bookRepository.findAll();
        List<BookDto> bookDto= books.stream().map(book -> modelMapper.map(books,BookDto.class)).collect(Collectors.toList());
        return bookDto;
    }

    public BookIdDto findByIsbn(String isbn)
    {

     return bookRepository.findBookByIsbn(isbn)
             .map(book -> new BookIdDto(book.getId(),book.getIsbn()))
             .orElseThrow(() -> new BookNotFoundExcepiton("Book could not found by isbn: " + isbn));
    }




}
