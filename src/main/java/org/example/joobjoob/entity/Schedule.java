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
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;


    private String dayOfWeek; // MON, TUE ...
    private String startTime; // e.g. 09:00
    private String endTime;
}