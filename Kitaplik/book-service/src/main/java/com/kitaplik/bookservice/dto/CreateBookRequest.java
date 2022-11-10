package com.kitaplik.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Validated
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
