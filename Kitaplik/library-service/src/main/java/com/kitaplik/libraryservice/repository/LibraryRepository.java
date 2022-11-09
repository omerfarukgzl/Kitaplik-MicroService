package com.kitaplik.libraryservice.repository;

import com.kitaplik.libraryservice.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<Library,String> {
}
