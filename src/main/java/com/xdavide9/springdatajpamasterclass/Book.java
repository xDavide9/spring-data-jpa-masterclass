package com.xdavide9.springdatajpamasterclass;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = "student")  // prevent circular reference
@EqualsAndHashCode(exclude = "student")
@Builder
@Entity(name = "Book")
@Table(name = "book")
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @SequenceGenerator(
            name = "book_sequence",
            sequenceName = "book_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "book_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;
    @Column(
            name = "book_name",
            nullable = false
    )
    private String bookName;
    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(
            name = "student_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "student_book_fk"
            ),
            nullable = false
    )
    private Student student;
}
