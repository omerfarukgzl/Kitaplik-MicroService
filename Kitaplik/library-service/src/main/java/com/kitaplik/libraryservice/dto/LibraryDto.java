package com.kitaplik.libraryservice.dto;

import com.kitaplik.bookservice.dto.BookDto;

import java.util.ArrayList;
import java.util.List;

public class LibraryDto {
    private String id;
    private List<BookDto> userBookList=new ArrayList<>();
}
