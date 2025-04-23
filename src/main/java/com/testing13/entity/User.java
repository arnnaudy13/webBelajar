package com.testing13.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_courses",  // The name of the join table
            joinColumns = @JoinColumn(name = "user_id"),  // The column in the join table that refers to the User entity
            inverseJoinColumns = @JoinColumn(name = "course_id")  // The column in the join table that refers to the Course entity
    )
    private Set<Course> courses = new HashSet<>();

    // Add this collection to represent the many-to-many relationship through the UserCourse entity
    @OneToMany(mappedBy = "user")
    private Set<UserCourse> userCourses = new HashSet<>();
}
