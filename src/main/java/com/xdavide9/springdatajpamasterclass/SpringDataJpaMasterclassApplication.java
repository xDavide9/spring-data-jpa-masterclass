package com.xdavide9.springdatajpamasterclass;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@Slf4j
public class SpringDataJpaMasterclassApplication {

    public SpringDataJpaMasterclassApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaMasterclassApplication.class, args);
    }

    @Bean   // experimenting with the methods provided by repository (standard crud operations)
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
            log.info("Saving maria");
            repository.save(maria);
            log.warn("Deleting maria");
            repository.deleteById(1L);
            log.info("Saving maria and john");
            repository.saveAll(List.of(maria, john));
            log.info("Fetching maria and john");
            List<Student> students = repository.findAll();
            students.forEach(System.out::println);
            log.info("Fetching student with id 3");
            repository.findById(3L).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Student with id 3 not found"));
            log.info("Fetching student with id 4");
            repository.findById(4L).ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Student with id 4 not found"));
        };
    }

}
