package com.testing13.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCourse {

    @EmbeddedId
    private UserCourseId id;

    @ManyToOne
    @MapsId("userId")  // This links the composite key's userId to the user entity
    private User user;

    @ManyToOne
    @MapsId("courseId")  // This links the composite key's courseId to the course entity
    private Course course;

    private String userEmail;  // Add userEmail to the join entity, if needed
}
