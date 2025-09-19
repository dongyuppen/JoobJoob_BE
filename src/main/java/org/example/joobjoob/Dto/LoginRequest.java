package org.example.joobjoob.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String studentNumber;
    private String password;
}