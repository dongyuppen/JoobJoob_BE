package org.example.joobjoob.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule { // 시간표 기능
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course; // 강의 명


    private String dayOfWeek; // 요일 MON, TUE ...
    private String startTime; // 시작 시간 (예: 09:00)
    private String endTime; // 종료 시간
}