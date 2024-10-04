package com.xdavide9.springdatajpamasterclass;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = "enrolments")
@ToString(exclude = "enrolments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course")
@Entity(name = "Course")
public class Course {
    @Id
    @SequenceGenerator(
            name = "course_sequence",
            sequenceName = "course_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "course_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;
    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String name;
    @Column(
            name = "department",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String department;

    @Builder.Default
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Enrolment> enrolments = new ArrayList<>();

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
