package org.example.joobjoob.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.joobjoob.entity.Course;
import org.example.joobjoob.entity.Enrollment;
import org.example.joobjoob.entity.Student;
import org.example.joobjoob.repository.CartItemRepository;
import org.example.joobjoob.repository.CourseRepository;
import org.example.joobjoob.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CartItemRepository cartItemRepository;
    private final NotificationService notificationService;


    @Transactional
    public Enrollment enroll(Student student, Long courseId){
// Lock course for update to avoid race conditions
        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new RuntimeException("해당 강의를 찾을 수 없습니다."));


// check already enrolled
        Optional<Enrollment> existing = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
        if(existing.isPresent()) throw new RuntimeException("이미 수강신청한 강의입니다.");


        if(course.getCurrentEnrollment() >= course.getCapacity()){
            throw new RuntimeException("정원이 가득 찼습니다.");
        }


        course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
        courseRepository.save(course);


        Enrollment e = Enrollment.builder().student(student).course(course).build();
        Enrollment saved = enrollmentRepository.save(e);


// If success and cart has the item, remove from cart
        cartItemRepository.findByCourseId(courseId).stream()
                .filter(ci -> ci.getStudent().getId().equals(student.getId()))
                .findFirst().ifPresent(ci -> cartItemRepository.delete(ci));


        return saved;
    }


    @Transactional
    public void cancelEnrollment(Student student, Long courseId){
        Enrollment e = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("수강 내역이 없습니다."));


// lock course
        Course course = courseRepository.findByIdForUpdate(courseId)
                .orElseThrow(() -> new RuntimeException("해당 강의를 찾을 수 없습니다."));


// delete enrollment
        enrollmentRepository.delete(e);
        course.setCurrentEnrollment(Math.max(0, course.getCurrentEnrollment() - 1));
        courseRepository.save(course);


// notify cart owners that a seat is available
        notificationService.notifySeatAvailable(courseId);
    }


    public List<Enrollment> getEnrollments(Student student){
        return enrollmentRepository.findByStudent(student);
    }
}