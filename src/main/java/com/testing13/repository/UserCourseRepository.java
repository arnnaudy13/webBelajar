
package com.testing13.repository;

import com.testing13.entity.UserCourse;
import com.testing13.entity.UserCourseId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository extends JpaRepository<UserCourse, UserCourseId> {
}

