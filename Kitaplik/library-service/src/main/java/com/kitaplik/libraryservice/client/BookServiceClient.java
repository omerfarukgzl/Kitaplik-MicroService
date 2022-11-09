package com.kitaplik.libraryservice.client;

import com.kitaplik.libraryservice.dto.BookDto;
import com.kitaplik.libraryservice.dto.BookIdDto;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "book-service", path = "/v1/book")
public interface BookServiceClient {

    Logger logger = LoggerFactory.getLogger(BookServiceClient.class);

    /*    default ResponseEntity<BookIdDto> getBookFallback(String isbn, Exception exception) {
        logger.info("Book not found by isbn " + isbn + ", returning default BookDto object");
        return ResponseEntity.ok(new BookIdDto("default-book", "default-isbn"));

        default ResponseEntity<BookDto> getBookByIdFallback(String bookId, Exception exception) {
        logger.info("Book not found by id " + bookId + ", returning default BookDto object");
        return ResponseEntity.ok(new BookDto(new BookIdDto("default-book", "isbn")));
    }


    }*/
    @GetMapping("/isbn/{isbn}")
   // @CircuitBreaker(name = "getBookByIsbnCircuitBreaker", fallbackMethod = "getBookFallback")
    ResponseEntity<BookIdDto> getBookByIsbn(@PathVariable(value = "isbn") String isbn);



    @GetMapping("/book/{bookId}")
   // @CircuitBreaker(name = "getBookByIdCircuitBreaker", fallbackMethod = "getBookByIdFallback")
    ResponseEntity<BookDto> getBookById(@PathVariable(value = "bookId") String bookId);


}