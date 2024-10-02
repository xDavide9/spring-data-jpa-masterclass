package com.xdavide9.springdatajpamasterclass;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = "Student")   // specify the name of the entity as good practice,
                            // default is class name anyways like specified
// @Table allows more granular control of uniqueContraints, table name, schema, indexes
@Table(
        name = "student",   // specify the name of the table as good practice,
                            // default is class name in lowercase like specified
        uniqueConstraints = {       // more granular control of unique constraints
                @UniqueConstraint(
                        name = "student_email_unique",  // choose your own name
                        columnNames = "email"   // specify the column(s) that should be unique thanks to that specific constraint
                )
        },
        schema = "public",   // default schema is public
        // can also add indexes the same way as unique constraints here
        indexes = {
                @Index(
                        name = "student_first_name_index",
                        columnList = "first_name"
                )
        }
)
public class Student {
    // use a sequence to generate the id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1  // how much the sequence should increment by
    )
    @GeneratedValue(
            generator = "student_sequence",
            strategy = SEQUENCE
    )
    @Column(
            name = "id",
            updatable = false
    )
    @Id
    private Long id;
    @Column(
            name = "first_name",    // specifying the column name is also good practice,
                                    // default is camel case version of the field name like specified
            nullable = false,   // NOT NULL sql constraint
            columnDefinition = "TEXT"   // postgresql specific column type
    )
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String lastName;
    @Column(
            name = "email",
            nullable = false,
            columnDefinition = "TEXT"
            // unique = true // UNIQUE sql constraint, when this constraint is specified in postgres the database creates a key
            // (which garanties uniqueness) and also an index that increases read speed (but decreases write speed)
            // note that an index can be created on any column even if it is not unique
            // commented because it's better to specify in @Table annotation above for more granular control
    )
    private String email;
    @Column(
            name = "age",
            nullable = false
    )
    private int age;
}
