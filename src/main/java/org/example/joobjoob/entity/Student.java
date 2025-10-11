package org.example.joobjoob.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String studentNumber; // 학번, 로그인 ID


    @Column(nullable = false)
    private String password;


    private String name;


    private String role; // STUDENT or ADMIN


    private LocalDateTime createdAt;

    // ✅ [추가] 학년과 학과(전공) 필드
    private String grade;
    private String department;

    // ✅ [추가] 최대 신청 가능 학점 필드
    private Integer maxCredits;


    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }
}