package org.example.joobjoob.repository;

import org.example.joobjoob.entity.CartItem;
import org.example.joobjoob.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByStudent(Student student);
    List<CartItem> findByCourseId(Long courseId);
}