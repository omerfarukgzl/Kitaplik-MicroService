package com.kitaplik.bookservice.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;

@Entity
@Table(name="books")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    private String title;
    private Integer bookYear;
    private String author;
    private String pressName;
    private String isbn;
}
