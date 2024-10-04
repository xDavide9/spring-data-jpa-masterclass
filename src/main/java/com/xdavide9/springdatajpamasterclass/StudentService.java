package com.xdavide9.springdatajpamasterclass;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StudentService {

    // @Transactional should be used in the service layer
    // for operations that update, or delete from the database to ensure consistency
    // it might be possible that it's necessary also when persisting complex types
    // a transaction makes sure that your application has ACID characteristics
    // ACID stands for Atomicity, Consistency, Isolation, Durability
    // Atomicity: all or nothing
    // Consistency: data is consistent before and after the transaction
    // Isolation: transactions are isolated from each other
    // Durability: once a transaction is committed, it will persist even in case of a system failure

    // from the docs:
    // If no custom rollback rules are configured in this annotation,
    // the transaction will roll back on RuntimeException and Error but not on checked exceptions.

    // therefore always throw unchecked exceptions in the service layer
    // never throw checked exceptions

    private StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional  // the transaction will work here because it's not a self-invocation
    public void fetchStudentBooks(Long studentId) {
        studentRepository.findById(studentId).ifPresent(student -> {
            List<Book> books = student.getBooks();
            System.out.println(student);
            System.out.println(books);
        });
    }

    @Transactional()
    public void fetchStudentEnrolments(Long studentId) {
        studentRepository.findById(studentId).ifPresent(student -> {
            List<Enrolment> enrolments = student.getEnrolments();
            System.out.println(student);
            System.out.println(enrolments);
        });
    }

    @Transactional  // wrap everything in a transaction to avoid
    public void persistStudentsWithEnrollment() {
        // First create students and courses
        Student maria = Student.builder()
                .firstName("Maria")
                .lastName("Jones")
                .email("maria@gmail.com")
                .age(21).build();
        Student john = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .age(36).build();
        Course math = Course.builder()
                .name("Math")
                .department("Mathematics").build();
        Course physics = Course.builder()
                .name("Physics")
                .department("Physics").build();

        // Create enrolments and maintain bidirectional relationship
        Enrolment mariaInMath = Enrolment.builder()
                .id(new EnrolmentId(maria.getId(), math.getId()))
                .createdAt(LocalDateTime.now())
                .student(maria)
                .course(math)
                .build();
        Enrolment mariaInPhysics = Enrolment.builder()
                .id(new EnrolmentId(maria.getId(), physics.getId()))
                .createdAt(LocalDateTime.now())
                .student(maria)
                .course(physics)
                .build();
        Enrolment johnInMath = Enrolment.builder()
                .id(new EnrolmentId(john.getId(), math.getId()))
                .createdAt(LocalDateTime.now())
                .student(john)
                .course(math)
                .build();

        // Add enrolments to students
        maria.addEnrolment(mariaInMath);
        maria.addEnrolment(mariaInPhysics);
        john.addEnrolment(johnInMath);

        // Now save the students, which will cascade and save the enrolments
        studentRepository.save(maria);
        studentRepository.save(john);

        // ensure consistency in java objects
        math.addEnrolment(mariaInMath);
        math.addEnrolment(johnInMath);
        physics.addEnrolment(mariaInPhysics);
        courseRepository.save(math);
        courseRepository.save(physics);

        // now fetch students or courses or enrolments themselves based on their repositories and custom queries
        // there is a lot of flexibility depending on what you are currently trying to do
    }

    @Transactional
    public void removeBookFromStudent(Long studentId, String bookName) {
        studentRepository.findById(studentId).ifPresent(student -> {
            List<Book> books = student.getBooks();
            Book bookToRemove = books.stream()
                    .filter(book -> book.getBookName().equals(bookName))
                    .findFirst()
                    .orElse(null);
            if (bookToRemove != null) {
                student.removeBook(bookToRemove);
                System.out.println("Book removed: " + bookToRemove.getBookName());
            } else {
                System.out.println("Book not found.");
            }
        });
    }
}
