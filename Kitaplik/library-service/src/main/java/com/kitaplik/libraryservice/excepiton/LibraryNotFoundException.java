package com.kitaplik.libraryservice.excepiton;

public class LibraryNotFoundException extends RuntimeException{

    public LibraryNotFoundException(String message) {
        super(message);
    }
}