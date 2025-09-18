package org.example.joobjoob.repository;

import org.example.joobjoob.entity.Enrollment;
import org.example.joobjoob.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    Long countByCourseId(Long courseId);
}