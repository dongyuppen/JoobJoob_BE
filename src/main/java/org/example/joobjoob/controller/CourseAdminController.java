package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.entity.Course;
import org.example.joobjoob.repository.CourseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class CourseAdminController {
    private final CourseRepository courseRepository;


    @PostMapping
    public ResponseEntity<?> create(@RequestBody Course course){
        course.setCurrentEnrollment(0);
        return ResponseEntity.ok(courseRepository.save(course));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        courseRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}