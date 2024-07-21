package com.example.BigProject_25.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 특정 경로에 대한 접근 허용 설정
                .antMatchers("/h2-console/**", "/taxi-requests/**", "/auth/**", "/static/**", "/css/**", "/js/**", "/images/**", "/lost-items/**", "/api/**", "/ask", "/flight-status", "/parking-fees").permitAll()
                .anyRequest().authenticated()  // 그 외 모든 요청은 인증된 사용자만 접근 가능
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .csrf().disable()  // 필요에 따라 CSRF 보호 비활성화
                .headers().frameOptions().disable()  // H2 콘솔을 사용하기 위해 프레임 옵션 비활성화
                .and()
                .cors();  // CORS 설정 추가
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
