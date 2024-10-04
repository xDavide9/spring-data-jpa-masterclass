package com.xdavide9.springdatajpamasterclass;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StudentService {

    private StudentRepository studentRepository;

    @Transactional  // the transaction will work here because it's not a self-invocation
    public void fetchStudentBooks(Long studentId) {
        studentRepository.findById(studentId).ifPresent(student -> {
            List<Book> books = student.getBooks();
            System.out.println(student);
            System.out.println(books);
        });
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
