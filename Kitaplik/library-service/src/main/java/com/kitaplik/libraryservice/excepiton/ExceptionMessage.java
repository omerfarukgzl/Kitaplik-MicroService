package com.kitaplik.libraryservice.excepiton;

public record ExceptionMessage (String timestamp,
                                int status,
                                String error,
                                String message,
                                String path){}
