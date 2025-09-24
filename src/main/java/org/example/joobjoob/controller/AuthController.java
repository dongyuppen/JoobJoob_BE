package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.Dto.LoginRequest;
import org.example.joobjoob.Dto.LoginResponse;
import org.example.joobjoob.entity.Student;
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


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupReq req){
        Student s = authService.signup(req.getStudentNumber(), req.getPassword(), req.getName(), "STUDENT");
        return ResponseEntity.ok(s);
    }


// login omitted; in production return JWT


    public static class SignupReq{
        private String studentNumber;
        private String password;
        private String name;
        public String getStudentNumber(){return studentNumber;} public void setStudentNumber(String s){this.studentNumber=s;}
        public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
        public String getName(){return name;} public void setName(String n){this.name=n;}
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Student student = studentRepository.findByStudentNumber(request.getStudentNumber())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 학번입니다."));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }

        // ✅ 수정된 메소드 호출: student 객체 전체를 전달
        String token = jwtTokenProvider.createToken(student);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}