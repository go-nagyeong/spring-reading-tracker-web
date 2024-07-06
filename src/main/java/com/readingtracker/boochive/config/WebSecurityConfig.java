package com.readingtracker.boochive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 추가 (TODO: 임시 < 403 에러 해결되면 삭제)
                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/","/login","/register","/assets/**","/api/auth/**")
//                        .permitAll() // 위 경로 권한 허가
//                        .anyRequest().authenticated() // 그 외 모든 경로 인증 필요
                        .anyRequest().permitAll() // 추가 (TODO: 임시)
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true) // 추가
                        .permitAll()
                )
//                .rememberMe()
                .logout((logout) -> logout // 추가
                        .invalidateHttpSession(true) // 전체 세션 초기화
                        .permitAll()
                );

        return http.build();
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("userPass"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
