package org.example.joobjoob.entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course { // 강의 정보
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String code; //강의 코드


    private String title; // 강의명


    @Column(length = 2000)
    private String description; // 설명


    private Integer credit; // 학점


    private Integer capacity; // 정원


    private Integer currentEnrollment = 0; // 현재 신청 인원


    private String semester; // 학기 (예: 2025-1)

    private String targetGrade;

    private String department;
}