package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.entity.CartItem;
import org.example.joobjoob.entity.Course;
import org.example.joobjoob.entity.Student;
import org.example.joobjoob.repository.CartItemRepository;
import org.example.joobjoob.repository.CourseRepository;
import org.example.joobjoob.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartItemRepository cartItemRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;


    // NOTE: for demo, studentNumber passed in request; replace with auth principal in real app
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody AddCartReq req){
        Student s = studentRepository.findByStudentNumber(req.getStudentNumber())
                .orElseThrow(() -> new RuntimeException("학생이 없습니다."));
        Course c = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
        CartItem item = CartItem.builder().student(s).course(c).build();
        return ResponseEntity.ok(cartItemRepository.save(item));
    }


    @GetMapping
    public ResponseEntity<List<CartItem>> myCart(@RequestParam String studentNumber){
        Student s = studentRepository.findByStudentNumber(studentNumber).orElseThrow();
        return ResponseEntity.ok(cartItemRepository.findByStudent(s));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        cartItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


    public static class AddCartReq{
        private String studentNumber;
        private Long courseId;
        public String getStudentNumber(){return studentNumber;} public void setStudentNumber(String s){this.studentNumber=s;}
        public Long getCourseId(){return courseId;} public void setCourseId(Long id){this.courseId=id;}
    }
}