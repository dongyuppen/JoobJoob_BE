package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.Dto.LoginRequest;
import org.example.joobjoob.Dto.LoginResponse;
import org.example.joobjoob.entity.Student;
import org.example.joobjoob.repository.EnrollmentRepository;
import org.example.joobjoob.repository.StudentRepository;
import org.example.joobjoob.security.JwtTokenProvider;
import org.example.joobjoob.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EnrollmentRepository enrollmentRepository; // ✅ EnrollmentRepository 주입

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupReq req){
        // ✅ [수정] 서비스 호출 시 maxCredits 전달 로직 제거
        Student s = authService.signup(
                req.getStudentNumber(),
                req.getPassword(),
                req.getName(),
                "STUDENT",
                req.getGrade(),
                req.getDepartment()
        );
        return ResponseEntity.ok(s);
    }

    // ✅ [수정] SignupReq DTO에서 maxCredits 관련 코드 모두 제거
    public static class SignupReq {
        private String studentNumber;
        private String password;
        private String name;
        private String grade;
        private String department;

        // ... 기존 getter/setter ...
        public String getStudentNumber(){return studentNumber;}
        public void setStudentNumber(String s){this.studentNumber=s;}
        public String getPassword(){return password;}
        public void setPassword(String p){this.password=p;}
        public String getName(){return name;}
        public void setName(String n){this.name=n;}
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Student student = studentRepository.findByStudentNumber(request.getStudentNumber())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 학번입니다."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }

        // --- ✅ [추가] 신청 가능 학점 계산 로직 ---
        // 1. 학생이 현재 신청한 총 학점 계산 (신청 내역 없으면 0)
        Integer enrolledCredits = enrollmentRepository.sumCreditsByStudentId(student.getId());
        if (enrolledCredits == null) {
            enrolledCredits = 0;
        }

        // 2. 학생의 최대 학점에서 현재 신청 학점을 빼서 신청 가능 학점 계산
        int availableCredits = student.getMaxCredits() - enrolledCredits;
        // --- ✅ 계산 로직 끝 ---

        // ✅ [수정] createToken 호출 시 계산된 availableCredits 전달
        String token = jwtTokenProvider.createToken(student, availableCredits);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}