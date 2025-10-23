package org.example.joobjoob.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatbotRequest {
    private String query; // 사용자의 질문
}