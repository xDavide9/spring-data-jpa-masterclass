package com.xdavide9.springdatajpamasterclass;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "Enrolment")
@Table(name = "enrolment")
@ToString
public class Enrolment {

    @EmbeddedId // use the composite key
    private EnrolmentId id;

    @ManyToOne  // use many to one for each piece of the composite key (many enrolments for a single student)
    @MapsId(value = "studentId") // maps the studentId from the composite key (java field)
    @JoinColumn(
            name = "student_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "enrolment_student_id_fk"
            )
    )
    private Student student;
    @ManyToOne  // use many to one for each piece of the composite key (many enrolments for a course student)
    @MapsId(value = "courseId") // maps the courseId from the composite key (java field)
    @JoinColumn(
            name = "course_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "enrolment_course_id_fk"
            )
    )
    private Course course;

    // by combining those two manyToOne we have created a manyToMany relationship

    // it's possible to add more columns
    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime createdAt;
}
