package org.example.joobjoob.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 보안 설정
@Configuration
public class SecurityConfig {

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 현재는 직접 JWT를 발급하는 방식은 빠져 있지만, 확장 가능하게 준비되어 있음
    // 인증 매니저 제공. 나중에 로그인 로직에서 AuthenticationManager를 이용해 사용자의 아이디/비밀번호를 검증할 수 있음
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // HTTP 보안 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // API는 누구나 접근 가능, 모든 요청 허용, 일단 테스트용으로 모두 접근 가능하게 열어둠
                )

                // 이런식으로 코드를 수정해서 특정 API를 관리자만 접근 가능하게 설정 가능
                /*
                .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/ws/**", "/api/server-time").permitAll() // 특정 API는 누구나 접근 가능
            .anyRequest().authenticated() // 나머지는 인증 필요
                 */

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}