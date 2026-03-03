package com.example.demo.config.oauth2;

import com.example.demo.user.model.AuthUserDetails;
import com.example.demo.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("OAuth 2.0 로그인 성공 디버그");
        AuthUserDetails user = (AuthUserDetails)authentication.getPrincipal();

        String jwt = jwtUtil.createToken(user.getIdx(), user.getUsername(), user.getRole());
        response.addHeader("Set-Cookie", "ATOKEN=" + jwt + "; Path=/");
        String redirectUrl = "http://localhost:5173/success";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }
}
