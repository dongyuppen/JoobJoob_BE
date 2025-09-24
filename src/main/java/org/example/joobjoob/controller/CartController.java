package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.entity.CartItem;
import org.example.joobjoob.entity.Course;
import org.example.joobjoob.entity.Student;
import org.example.joobjoob.repository.CartItemRepository;
import org.example.joobjoob.repository.CourseRepository;
import org.example.joobjoob.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody AddCartReq req) {
        Student student = studentRepository.findByStudentNumber(req.getStudentNumber())
                .orElseThrow(() -> new RuntimeException("학생을 찾을 수 없습니다."));

        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));

        // --- ✅ 아래 로직 추가 ---
        // 1. 학생 ID와 강의 ID로 이미 수강바구니에 있는지 확인
        Optional<CartItem> existingCartItem = cartItemRepository.findByStudentIdAndCourseId(student.getId(), course.getId());

        // 2. 만약 존재한다면, 에러 메시지와 함께 409 Conflict 응답 반환
        if (existingCartItem.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 상태 코드
                    .body("이미 수강바구니에 담긴 과목입니다."); // 프론트엔드에 표시될 메시지
        }
        // --- ✅ 추가 로직 끝 ---

        CartItem newItem = CartItem.builder().student(student).course(course).build();
        return ResponseEntity.ok(cartItemRepository.save(newItem));
    }

    // ... (myCart, delete, AddCartReq 클래스는 그대로) ...
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