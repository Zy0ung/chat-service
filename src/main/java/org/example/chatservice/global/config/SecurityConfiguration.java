package org.example.chatservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author jiyoung
 */
@Configuration
public class SecurityConfiguration {

    @Order(2)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request ->
                        request.anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // filterChain 작동 순서적용
    @Order(1)
    @Bean
    public SecurityFilterChain consultantSecurityFilterChain(HttpSecurity http, HttpSecurity httpSecurity)
            throws Exception {
        http
                .securityMatcher("/consultants/**", "/login")
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "/consultants").permitAll()
                        .anyRequest().hasRole("CONSULTANT"))
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}