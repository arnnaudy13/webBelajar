package com.testing13.controller;

import com.testing13.dto.CourseEnrollmentRequest;
import com.testing13.entity.Course;
import com.testing13.entity.User;
import com.testing13.entity.UserCourse;
import com.testing13.entity.UserCourseId;
import com.testing13.repository.CourseRepository;
import com.testing13.repository.UserCourseRepository;
import com.testing13.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserCourseRepository userCourseRepository;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollCourse(
            @RequestBody CourseEnrollmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Create a new UserCourse object to represent the enrollment
        UserCourse userCourse = UserCourse.builder()
                .user(user)
                .course(course)
                .userEmail(user.getEmail())
                .id(new UserCourseId(user.getId(), course.getId())) // Set the composite key
                .build();

        // Save the UserCourse object to the database
        userCourseRepository.save(userCourse);

        return ResponseEntity.ok("Enrolled in course successfully.");
    }
}
