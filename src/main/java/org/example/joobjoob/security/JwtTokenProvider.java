package org.example.joobjoob.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.joobjoob.entity.Student;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// JWT Util 클래스
@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityInMilliseconds = 1000 * 60 * 60; // 1시간 유효

    // 토큰 생성 메소드 수정
    public String createToken(Student student) { // 파라미터를 Student 객체로 변경
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(student.getStudentNumber()) // 'sub' 클레임에 학번 설정
                .claim("role", student.getRole())       // 'role' 클레임에 역할 설정
                .claim("name", student.getName())       // ✅ 'name' 클레임을 추가하여 학생 이름 저장
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    // 토큰에서 학번 추출
    public String getStudentNumber(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰에서 role 추출
    public String getRole(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("role");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}