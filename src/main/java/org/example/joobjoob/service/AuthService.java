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

    // ✅ [수정] maxCredits 파라미터 제거
    public Student signup(String studentNumber, String rawPassword, String name, String role, String grade, String department) {
        if(studentRepository.findByStudentNumber(studentNumber).isPresent()){
            throw new RuntimeException("이미 존재하는 학번입니다.");
        }

        // --- ✅ [추가] 학번에 따라 maxCredits를 자동 설정하는 로직 ---
        int determinedMaxCredits;
        // 학번의 앞 4자리를 연도로 추출
        String yearPrefix = studentNumber.substring(0, 4);

        if (yearPrefix.equals("2023") || yearPrefix.equals("2024") || yearPrefix.equals("2025")) {
            determinedMaxCredits = 24;
        } else if (yearPrefix.equals("2020") || yearPrefix.equals("2021") || yearPrefix.equals("2022")) {
            determinedMaxCredits = 21;
        } else {
            determinedMaxCredits = 18; // 그 외 학번을 위한 기본값
        }
        // --- 로직 끝 ---

        Student s = Student.builder()
                .studentNumber(studentNumber)
                .password(passwordEncoder.encode(rawPassword))
                .name(name)
                .role(role == null ? "STUDENT" : role)
                .grade(grade)
                .department(department)
                .maxCredits(determinedMaxCredits) // ✅ 자동 설정된 값으로 저장
                .build();
        return studentRepository.save(s);
    }
}