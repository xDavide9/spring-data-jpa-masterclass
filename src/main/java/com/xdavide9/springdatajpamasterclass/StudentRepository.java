package com.xdavide9.springdatajpamasterclass;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// can extend JpaRepository, PagingAndSortingRepository, CrudRepository
// JpaRepository extends PagingAndSortingRepository which extends CrudRepository
@Repository
public interface StudentRepository  extends JpaRepository<Student, Long> {
    // Spring Data JPA will automatically generate the query based on the naming convention of the method

    // return optional because we know that email is unique
    // even if Spring Data JPA can generate queries automatically it's good practice to always use @Query to have full control
    // ?1 is a positional parameter that maps to the first argument passed to the method ?2 to the second and so on...
    @Query("SELECT s FROM Student s WHERE s.email = ?1")
    // Student is the entity name specified in @Entity in Student.java
    // The querying language is JPQL (Java Persistence Query Language) which garantees that if you switch database at some point the queries will still work
    Optional<Student> findStudentByEmail(String email);

    // return list because there might be more people with same firstName and age
    @Query("SELECT s FROM Student s WHERE s.firstName = ?1 AND s.age = ?2")
    // now I could even change that long name to something shorter
    List<Student> findStudentsByFirstNameEqualsAndAgeEquals(String firstName, Integer age);

    // I could also use native sql queries but it's better to stick with JPQL
    @Query(value = "SELECT * FROM student WHERE first_name = ?1 AND age = ?2", nativeQuery = true)
    List<Student> findStudentsByFirstNameEqualsAndAgeEqualsNative(String firstName, Integer age);

    // instead of positional parameters it's possible to use named parameters for more complex queries
    @Query("SELECT s FROM Student s WHERE s.firstName = :firstName AND s.age = :age")
    List<Student> findStudentsByFirstNameEqualsAndAgeEqualsNamedParameters(
            @Param("firstName") String firstName,
            @Param("age") Integer age);

    // all the methods above return the Student entity as Java class
    // but if my query is not about selecting data but updating or deleting I can use @Modifying
    // additionally I should use @Transactional
    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.firstName = ?1")
    int deleteStudentsByFirstName(String firstName);    // returns the number of rows affected
}
