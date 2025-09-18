package org.example.joobjoob.service;

import lombok.RequiredArgsConstructor;
import org.example.joobjoob.entity.CartItem;
import org.example.joobjoob.repository.CartItemRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final CartItemRepository cartItemRepository;
    private final SimpMessagingTemplate messagingTemplate; // for WebSocket push


    public void notifySeatAvailable(Long courseId){
// find all cart items for the course
        List<CartItem> carts = cartItemRepository.findByCourseId(courseId);
        for(CartItem c : carts){
            String destination = "/topic/notifications/" + c.getStudent().getStudentNumber();
            messagingTemplate.convertAndSend(destination, "좌석이 남았습니다: " + c.getCourse().getTitle());
        }
    }
}