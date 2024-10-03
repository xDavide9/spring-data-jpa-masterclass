package com.xdavide9.springdatajpamasterclass;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity(name = "StudentIdCard")
@Table(
        name = "student_id_card",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "student_id_card_number_unique",
                        columnNames = "card_number"
                )
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentIdCard {
    @Id
    @SequenceGenerator(
            name = "student_id_card_sequence",
            sequenceName = "student_id_card_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "student_id_card_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;
    @Column(
            name = "card_number",
            nullable = false,
            columnDefinition = "TEXT",
            length = 15
    )
    private String cardNumber;

    // in relations you use the actual Java type of the entity not the id type
    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER // eager is default for one to one while for other relationships it's lazy to avoid loading too much data at once
    )   // this is the owning side of the relationship
    // therefore it's possible to specify which cascade type should be used
    // cascade types refer to the entity manager operations (provided by hibernate which is the default in spring data jpa)
    // and specifically CascadeType.ALL means that all operations will be cascaded to the target of the association
    // For example when a student is created it's in the transient state and when it's saved it's in the managed state, from there it can go
    // to the detached state, removed state, in the persistentContext that is a 1 level cache between the database and the application itself but all of this
    // is actually abstracted by spring data jpa and it's ok to cascade all operations if needed
    // these operations are persist, remove, merge, detach, refresh and are based on the sql insert, delete, update operations
    // see diagram for more details about the entity lifecycle
    @JoinColumn(
            name = "student_id",    // this is the name of the column in the student_id_card table
            referencedColumnName = "id",    // this is the name of the column in the student table and they are joined together
            foreignKey = @ForeignKey(   // rename the foreign key constraint
                    name = "student_id_card_fk"
            )
    )
    private Student student;
}
