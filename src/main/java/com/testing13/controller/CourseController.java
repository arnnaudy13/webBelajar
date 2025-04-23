package com.testing13.controller;

import com.testing13.dto.CourseRequest;
import com.testing13.entity.Course;
import com.testing13.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepo;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createCourse(@RequestBody Course course) {
        courseRepo.save(course);
        return ResponseEntity.ok("Course created");
    }


}
