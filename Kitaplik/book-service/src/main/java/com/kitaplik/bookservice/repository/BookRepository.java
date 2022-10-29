package com.kitaplik.bookservice.repository;

import com.kitaplik.bookservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,String> {


    Optional<Book> findBookByIsbn(String isbn);


}
