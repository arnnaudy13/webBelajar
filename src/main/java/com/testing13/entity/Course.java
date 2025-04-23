package com.testing13.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String title;
    private String description;
    private String thumbnailUrl;

    @ManyToMany(mappedBy = "courses")
    private Set<User> users = new HashSet<>();

}

