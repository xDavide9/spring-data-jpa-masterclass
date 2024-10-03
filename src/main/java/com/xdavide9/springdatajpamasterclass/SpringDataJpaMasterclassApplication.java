package com.xdavide9.springdatajpamasterclass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    CommandLineRunner commandLineRunnerRelations(StudentRepository studentRepository, StudentIdCardRepository studentIdCardRepository) {
        return args -> {
            Student maria = Student.builder()
                    .firstName("Maria")
                    .lastName("Jones")
                    .email("maria@gmail.com")
                    .age(21).build();
            StudentIdCard studentIdCard = StudentIdCard.builder()
                    .cardNumber("1234567890")
                    .student(maria).build();
            // this is working even if the studentRepository was not used to save the student first because of the cascade specified in StudentIdCard
            studentIdCardRepository.save(studentIdCard);

            // thanks to a bidirectional relationship we can see the card from the student too
            studentRepository.findById(1L)
                    .ifPresent(System.out::println);

            // we can see the student from the card
            studentIdCardRepository.findById(1L)
                    .ifPresent(System.out::println);

            // deleting also the student card thanks to the orphanRemoval = true in Student side of relationship
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
