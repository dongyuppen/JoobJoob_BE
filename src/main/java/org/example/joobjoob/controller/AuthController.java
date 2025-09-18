package org.example.joobjoob.controller;

import lombok.*;
import org.example.joobjoob.entity.Student;
import org.example.joobjoob.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


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
}