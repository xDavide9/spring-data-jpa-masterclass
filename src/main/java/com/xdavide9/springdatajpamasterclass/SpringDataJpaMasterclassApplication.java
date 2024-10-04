package com.xdavide9.springdatajpamasterclass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@Slf4j
public class SpringDataJpaMasterclassApplication {

    // when working with spring data jpa it's important to explict configuration about columns and tables names in sql @Column @JoinColumn @Table...
    // but there are some other annotations that work with java members like @Entity (name of the class same as entity) mappedBy uses the property name in the owning side of the relationship...

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaMasterclassApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunnerRelations(StudentRepository studentRepository, StudentIdCardRepository studentIdCardRepository, StudentService studentService) {
        return args -> {
            Student maria = Student.builder()
                    .firstName("Maria")
                    .lastName("Jones")
                    .email("maria@gmail.com")
                    .age(21).build();
            StudentIdCard studentIdCard = StudentIdCard.builder()
                    .cardNumber("1234567890")
                    .student(maria).build();
            Book book = Book.builder()
                    .bookName("The Alchemist")
                    .createdAt(LocalDateTime.now())
                    .student(maria).build();
            Book book1 = Book.builder()
                    .bookName("Clean Code")
                    .createdAt(LocalDateTime.now().minusYears(1L))
                    .student(maria).build();
            Book book2 = Book.builder()
                    .bookName("The Pragmatic Programmer")
                    .createdAt(LocalDateTime.now().minusYears(2L))
                    .student(maria).build();
            maria.addBook(book);
            maria.addBook(book1);
            maria.addBook(book2);
            maria.setStudentIdCard(studentIdCard);

            // persisting the student will persist the card and the books thanks to the cascade operations
            // Note that the student owns the relationship with books but the card owns the relationship with the student
            // so it doesn't matter who is the owning side
            studentRepository.save(maria);

            // thanks to a bidirectional relationship we can see the card from the student too
            // thanks to the default fetch Type LAZY we can't see books from this query (a join is not performed, it's many to one, one to many or many to many)
            // but we can see the studentIdCard because it has fetchType EAGER (one to one)
            studentRepository.findById(1L)
                    .ifPresent(System.out::println);

            // move the logic when we want to lazily load the student's books to a service so that
            // @Transactional will correctly create a transaction and avoid a self-invocation
            // Note that a bookRepository doesn't even exist because the books are completely
            // managed by the Student to whom they belong
            studentService.fetchStudentBooks(1L);

            // let's try to remove a book from the student and lazily fetch again
            // all these operations should happen inside transactions so we move to the service
            studentService.removeBookFromStudent(1L, "Clean Code");
            studentService.fetchStudentBooks(1L);

            // we can see the student from the card
            studentIdCardRepository.findById(1L)
                    .ifPresent(System.out::println);

            // deleting also the student card and books thanks to the orphanRemoval = true in Student side of relationships
            studentRepository.deleteById(1L);
        };
    }

    //@Bean   // experimenting with the methods provided by repository (standard crud operations)
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> {
            Student maria = Student.builder()
                    .firstName("Maria")
                    .lastName("Jones")
                    .email("maria@gmail.com")
                    .age(21).build();
            Student john = Student.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("joh@gmail.com")
                    .age(36).build();
//            log.info("Saving maria");
//            repository.save(maria);
//            log.warn("Deleting maria");
//            repository.deleteById(1L);
            log.info("Saving maria and john");
            repository.saveAll(List.of(maria, john));
//            log.info("Fetching maria and john");
//            List<Student> students = repository.findAll();
//            students.forEach(System.out::println);
//            log.info("Fetching student with id 3");
//            repository.findById(3L).ifPresentOrElse(
//                    System.out::println,
//                    () -> System.out.println("Student with id 3 not found"));
//            log.info("Fetching student with id 4");
//            repository.findById(4L).ifPresentOrElse(
//                    System.out::println,
//                    () -> System.out.println("Student with id 4 not found"));
//            log.info("Getting maria by email");
//            repository.findStudentByEmail("maria@gmail.com").ifPresentOrElse(
//                    System.out::println,
//                    () -> System.out.println("Student with email maria@gmail.com not found"));
//            log.info("Getting students with first name John and age 36 using JPQL");
//            repository.findStudentsByFirstNameEqualsAndAgeEquals("John", 36).forEach(System.out::println);
//            log.info("Getting students with first name John and age 36 using JPQL named parameters");
//            repository.findStudentsByFirstNameEqualsAndAgeEqualsNamedParameters("John", 36).forEach(System.out::println);
//            log.info("Getting students with first name John and age 36 using a native query");
//            repository.findStudentsByFirstNameEqualsAndAgeEqualsNative("John", 36).forEach(System.out::println);
//            log.warn("Deleting students with first name John, rows affect: {}", repository.deleteStudentsByFirstName("John"));
            log.info("Getting students in ascending order by first name");
            repository.findAll(Sort.by("firstName").ascending()).forEach(System.out::println);  // this is implemented by PagingAndSortingRepository
            log.info("Saving maria and john 4 times with different emails");
            for (int i = 0; i < 4; i++) {
                repository.save(Student.builder()
                        .firstName("Maria")
                        .lastName("Jones")
                        .email(String.format("maria%d@gmail.com", i))
                        .age(21).build());
                repository.save(Student.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .email(String.format("john%d@gmail.com", i))
                        .age(36).build());
            }
            PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("firstName").ascending());
            log.info("Getting students in ascending order by first name with pagination");
            Page<Student> page = repository.findAll(pageRequest);
            log.info("Total pages: {}", page.getTotalPages());
            log.info("Page: {}", page.getNumber() + 1);
            if (page.hasContent())
                page.forEach(System.out::println);
            while(page.hasNext()) {
                page = repository.findAll(page.nextPageable());
                log.info("Page: {}", page.getNumber() + 1);
                if (page.hasContent())
                    page.forEach(System.out::println);
            }
        };
    }

}
