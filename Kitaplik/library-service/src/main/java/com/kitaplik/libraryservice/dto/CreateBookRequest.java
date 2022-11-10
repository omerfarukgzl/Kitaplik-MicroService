package com.kitaplik.libraryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {
    private String id;
    @NotEmpty
    private String isbn;
    @NotEmpty
    private String title;
    @NotEmpty
    private Integer bookYear;
    @NotEmpty
    private String author;
    @NotEmpty
    private String pressName;
}
