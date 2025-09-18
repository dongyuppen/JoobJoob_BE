package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.entity.Enrollment;
import org.example.joobjoob.entity.Student;
import org.example.joobjoob.repository.StudentRepository;
import org.example.joobjoob.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final StudentRepository studentRepository;


    @PostMapping
    public ResponseEntity<?> enroll(@RequestBody EnrollReq req){
        Student s = studentRepository.findByStudentNumber(req.getStudentNumber()).orElseThrow();
        Enrollment e = enrollmentService.enroll(s, req.getCourseId());
        return ResponseEntity.ok(e);
    }


    @DeleteMapping
    public ResponseEntity<?> cancel(@RequestBody EnrollReq req){
        Student s = studentRepository.findByStudentNumber(req.getStudentNumber()).orElseThrow();
        enrollmentService.cancelEnrollment(s, req.getCourseId());
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<List<Enrollment>> myEnrollments(@RequestParam String studentNumber){
        Student s = studentRepository.findByStudentNumber(studentNumber).orElseThrow();
        return ResponseEntity.ok(enrollmentService.getEnrollments(s));
    }


    public static class EnrollReq{
        private String studentNumber;
        private Long courseId;
        public String getStudentNumber(){return studentNumber;} public void setStudentNumber(String s){this.studentNumber=s;}
        public Long getCourseId(){return courseId;} public void setCourseId(Long id){this.courseId=id;}
    }
}