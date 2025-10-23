package org.example.joobjoob.controller;

import lombok.RequiredArgsConstructor;
import org.example.joobjoob.Dto.ChatbotRequest;
import org.example.joobjoob.Dto.ChatbotResponse;
import org.example.joobjoob.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/query")
    public ResponseEntity<ChatbotResponse> handleQuery(@RequestBody ChatbotRequest request) {
        if (request.getQuery() == null || request.getQuery().isBlank()) {
            return ResponseEntity.badRequest().body(new ChatbotResponse("질문을 입력해주세요."));
        }
        String answer = chatbotService.handleQuery(request.getQuery());
        return ResponseEntity.ok(new ChatbotResponse(answer));
    }
}