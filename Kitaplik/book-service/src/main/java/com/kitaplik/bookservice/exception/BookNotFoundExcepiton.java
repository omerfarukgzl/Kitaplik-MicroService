package com.kitaplik.bookservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundExcepiton extends RuntimeException {
    public BookNotFoundExcepiton(String s) {
        super(s);
    }
}
