package org.example.joobjoob.repository;

import org.example.joobjoob.entity.Enrollment;
import org.example.joobjoob.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Query import 추가
import org.springframework.data.repository.query.Param; // Param import 추가
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    Long countByCourseId(Long courseId);

    // ✅ [추가] 학생 ID를 기반으로 신청한 총 학점을 계산하는 쿼리
    @Query("SELECT SUM(e.course.credit) FROM Enrollment e WHERE e.student.id = :studentId")
    Integer sumCreditsByStudentId(@Param("studentId") Long studentId);
}