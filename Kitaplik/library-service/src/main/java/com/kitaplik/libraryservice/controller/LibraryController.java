package com.kitaplik.libraryservice.controller;

import com.kitaplik.libraryservice.dto.CreateBookRequest;
import com.kitaplik.libraryservice.dto.AddBookRequest;
import com.kitaplik.libraryservice.dto.LibraryDto;
import com.kitaplik.libraryservice.service.LibraryService;
import org.hibernate.cfg.Environment;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RefreshScope
@RequestMapping("/v1/library")
public class LibraryController {
    Logger logger = LoggerFactory.getLogger(LibraryController.class);
    private final LibraryService libraryService;
   // private final Environment environment;

   /* @Value("${library.service.count}")
    private String count;*/

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
        //this.environment = environment;
    }

    @GetMapping("{id}")
    public ResponseEntity<LibraryDto> getLibraryById(@PathVariable String id) {
        return ResponseEntity.ok(libraryService.getAllBooksInLibraryById(id));
    }

    @PostMapping
    public ResponseEntity<LibraryDto> createLibrary() {
       // logger.info("Library created on port number " + environment.getProperty("local.server.port"));

        return ResponseEntity.ok(libraryService.createLibrary());
    }
    @PostMapping("{id}")
    public ResponseEntity<LibraryDto> createBookToLibrary(@RequestBody CreateBookRequest createBookRequest,@PathVariable String id) {
        // logger.info("Library created on port number " + environment.getProperty("local.server.port"));
           LibraryDto libraryDto= libraryService.createBookToLibrary(createBookRequest,id);
        return ResponseEntity.ok(libraryDto);
    }

    @PutMapping
    public ResponseEntity<Void> addBookToLibrary(@RequestBody AddBookRequest request) {
        libraryService.addBookToLibrary(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllLibraries() {
        return ResponseEntity.ok(libraryService.getAllLibraries());
    }

  /*  @GetMapping("/count")
    public ResponseEntity<String> getCount() {
        return ResponseEntity.ok("Library count is" + count);
    }*/
}
