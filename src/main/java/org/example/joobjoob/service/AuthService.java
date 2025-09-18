package org.example.joobjoob.service;

import lombok.RequiredArgsConstructor;
import org.example.joobjoob.entity.Student;
import org.example.joobjoob.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;


    public Student signup(String studentNumber, String rawPassword, String name, String role) {
        if(studentRepository.findByStudentNumber(studentNumber).isPresent()){
            throw new RuntimeException("이미 존재하는 학번입니다.");
        }
        Student s = Student.builder()
                .studentNumber(studentNumber)
                .password(passwordEncoder.encode(rawPassword))
                .name(name)
                .role(role == null ? "STUDENT" : role)
                .build();
        return studentRepository.save(s);
    }


// login should verify password and issue JWT; omitted for brevity
}