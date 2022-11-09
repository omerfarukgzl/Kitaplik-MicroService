package com.kitaplik.libraryservice.dto;

import com.kitaplik.libraryservice.dto.BookDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryDto {
    private String id;
    private List<BookDto> userBookList=new ArrayList<>();

}
