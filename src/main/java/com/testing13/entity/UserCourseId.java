package com.testing13.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
public class UserCourseId implements Serializable {

    private Long userId;
    private Long courseId;

    public UserCourseId() {}

    public UserCourseId(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }
}
