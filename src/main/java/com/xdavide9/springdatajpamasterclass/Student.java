package com.xdavide9.springdatajpamasterclass;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"studentIdCard", "books", "enrolments"})    // excluding  studentIdCard to avoid a circular reference because of the bidirectional relationship
@EqualsAndHashCode(exclude = {"studentIdCard", "books", "enrolments"})    // excluding books and enrolments because of lazy loading so it's not always garanteed to have books loaded
@Entity(name = "Student")   // specify the name of the entity as good practice this is used in queries
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
@Builder
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

    // @Transient is used to specify that a field should not be persisted to the database

    // bidirectional one to one with StudentIdCard (it's fine for one to one but not so good for other relathionships)
    @OneToOne(
            mappedBy = "student",  // specify the java field property in the owning side of the relationship
            // while it's tecnically possible to specify cascade here it's better to specify it in the owning side of the relationship
            orphanRemoval = true, // only specify this on the side of the relationship that matters more...
            // example if i delete a student i also want to delete his card but if i delete a card i don't want to delete the student
            // NOTE it doesn't really have anything to do with the owning side of the relationship
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    private StudentIdCard studentIdCard;

    // one to many implemented via the aggregate pattern (could consider making this the owning side of the relationship)
    // bidirectional one to many with Book
    // books are intended in this case as exclusive property of a single student so if the student is deleted also his books go away
    // if we wanted to make it so that a single book could be owned by more students it would be a many to many relationship
    @OneToMany(
            mappedBy = "student",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.LAZY  // default fetch type for one to many
    )
    @Builder.Default    // always initialize this field with the builder
    private List<Book> books = new ArrayList<>();

    public boolean addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
            book.setStudent(this);  // maintain the bidirectional relationship
            return true;
        }
        return false;
    }

    public boolean removeBook(Book book) {
        if (books.contains(book)) {
            books.remove(book);
            book.setStudent(null);
            return true;
        }
        return false;
    }

//    // simple many to many created with a join table
//    // straightforward but does not allow for additional columns in the join table
//    @ManyToMany(
//            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
//            fetch = FetchType.LAZY
//    )
//    @JoinTable(
//            name = "enrolment",
//            joinColumns = @JoinColumn(  // the columns to join in this class (Student)
//                    name = "student_id",
//                    referencedColumnName = "id"
//            ),
//            inverseJoinColumns = @JoinColumn(       // the columns to join in the other side of the relationship (Course)
//                    name = "course_id",
//                    referencedColumnName = "id"
//            )
//    )
//    @Builder.Default
//    private List<Course> courses = new ArrayList<>();
//
//    // aggregate pattern methods
//    public boolean addCourse(Course course) {
//        if (!courses.contains(course)) {
//            courses.add(course);
//            return true;
//        }
//        return false;
//    }
//
//    public boolean removeCourse(Course course) {
//        if (courses.contains(course)) {
//            courses.remove(course);
//            return true;
//        }
//        return false;
//    }

    // it's better to use a manually created entity
    @Builder.Default
    @OneToMany(
            cascade = CascadeType.ALL,
            // more students could partecipate in it
            fetch = FetchType.LAZY,
            mappedBy = "student",    // student field in Enrolment class
            orphanRemoval = true
    )
    private List<Enrolment> enrolments = new ArrayList<>();

    // it looks similar to before with the aggregate pattern but actually
    // student is not in charge of managing the courses
    // because they exist on their own and have their own repository
    // managing the bidirectional relationship is the most complex here
    public boolean addEnrolment(Enrolment enrolment) {
        if (!enrolments.contains(enrolment)) {
            enrolments.add(enrolment);
            return true;
        }
        return false;
    }
    public boolean removeEnrolment(Enrolment enrolment) {
        if (enrolments.contains(enrolment)) {
            enrolments.remove(enrolment);
            return true;
        }
        return false;
    }

}
