package com.xdavide9.springdatajpamasterclass;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable // this is a composite key
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode  // these methods are required for composite keys
public class EnrolmentId {
    @Column(
            name = "student_id",
            updatable = false
    )
    private Long studentId;
    @Column(
            name = "course_id",
            updatable = false
    )
    private Long courseId;
}
