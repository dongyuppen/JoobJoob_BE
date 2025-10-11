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

    // ✅ [수정] grade와 department를 파라미터로 추가
    public Student signup(String studentNumber, String rawPassword, String name, String role, String grade, String department) {
        if(studentRepository.findByStudentNumber(studentNumber).isPresent()){
            throw new RuntimeException("이미 존재하는 학번입니다.");
        }
        Student s = Student.builder()
                .studentNumber(studentNumber)
                .password(passwordEncoder.encode(rawPassword))
                .name(name)
                .role(role == null ? "STUDENT" : role)
                .grade(grade) // ✅ grade 정보 저장
                .department(department) // ✅ department 정보 저장
                .build();
        return studentRepository.save(s);
    }
}