package org.example.joobjoob.repository;

import org.example.joobjoob.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
}