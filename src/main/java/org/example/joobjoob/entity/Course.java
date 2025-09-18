package org.example.joobjoob.entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String code;


    private String title;


    @Column(length = 2000)
    private String description;


    private Integer credit;


    private Integer capacity; // 정원


    private Integer currentEnrollment = 0; // 현재 신청 인원


    private String semester;
}