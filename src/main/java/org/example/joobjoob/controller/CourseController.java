package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.entity.Course;
import org.example.joobjoob.repository.CourseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courseRepository;


    @GetMapping
    public ResponseEntity<List<Course>> list(){
        return ResponseEntity.ok(courseRepository.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Course> detail(@PathVariable Long id){
        return ResponseEntity.of(courseRepository.findById(id));
    }
}