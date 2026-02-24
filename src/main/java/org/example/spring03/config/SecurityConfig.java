package org.example.spring03.config;

/*
기존 스프링 시큐리티의 로그인 처리 방식

1. UsernamePasswordAuthenticationFilter 에서 ID(username), PW(password) 를 받아서             (컨트롤러 역할)
2. UsernamePasswordAuthenticationToken 객체에 담아서                                          (Dto 역할)
3. AuthenticationManager 인터페이스를 상속받은 ProviderManager 객체의 authenticate 메소드 실행    (서비스 메소드 실행 역할)

4. 3번에서 실행된 메소드에서 AbstractUserDetailsAuthenticationProvider 객체의 authenticate 메소드 실행
5. 4번에서 실행된 메소드에서 retrieveUser 메소드 실행하고 retrieveUser메소드에서 InMemoryUserDetailsManager 객체의 loadUserByUsername 메소드 실행
6. loadUserByUsername 메소드에서 사용자 정보를 조회해서 해당 하는 사용자가 있으면 UserDetails 객체를 반환
7. 8. 9. 반환받은 걸 확인해서 세션에 사용자 인증 정보 저장
 */

/*

요청 바꾸기 : UsernamePasswordAuthenticationFilter의 attemptAuthentication 메소드를 재정의
사용자 확인 : UserDetailsService의 loadUserByUsername 메소드를 재정의
응답 바꾸기 : UsernamePasswordAuthenticationFilter의 successfulAuthentication 메소드 재정의
*/

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final LoginFilter loginFilter;
    private final JwtFilter jwtFilter;


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 적용
        return source;
    }




    @Bean // 개발자가 직접 개발한 코드가 아닌 클래스의 객체를 스프링의 빈으로 등록하려고 할 때 사용
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // 특정 URI로 접속했을 때 권한 설정하는 부분
        // .permitAll() 전부 허용
        // .hasRole("ADMIN") AuthUserDetails 객체에서 ROLE_ADMIN 권한을 가진 사용자만 허용
        // .authenticated() 는 로그인 한 사용자만 허용
        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/user/verify", "/user/login", "/login", "/user/signup").permitAll()
                        .requestMatchers("/test/ex01").permitAll()
                        .requestMatchers("/test/ex02").authenticated()
                        .requestMatchers("/test/ex03").hasRole("USER")
                        .requestMatchers("/test/ex04").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );

        // CSRF 방어 기능을 중지하는 코드
        http.csrf(AbstractHttpConfigurer::disable);
        // basic 로그인 방식 사용 안하도록 설정
        http.httpBasic(AbstractHttpConfigurer::disable);
        // form 로그인 방식 사용 안하도록 설정
        http.formLogin(AbstractHttpConfigurer::disable);

        // 기존 필터 대신에 내가 구현한 필터로 교체
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
