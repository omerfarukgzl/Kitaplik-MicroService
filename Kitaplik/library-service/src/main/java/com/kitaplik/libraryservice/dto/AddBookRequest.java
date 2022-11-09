package com.kitaplik.libraryservice.dto;

import com.kitaplik.bookservice.dto.BookIdDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBookRequest {
    private String id;
    private String isbn;
}
