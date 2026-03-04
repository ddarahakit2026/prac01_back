package com.example.demo.config.oauth2;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2CancelInterceptorFilter extends OncePerRequestFilter {

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // OAuth 요청인지 확인 가능, 커스텀한 URL을 사용해도 가능
        if (request.getRequestURI().startsWith("/login/oauth2/code/")) {

            // 동의 화면에서 취소하거나 구글, 카카오가 보낸 에러를 확인 가능, 에러에 따른 처리도 가능(예외 처리, BaseReponse 형태로 응답 등)
            String error = request.getParameter("error");
            if (error != null) {
                // 여기서 loadAuthorizationRequest 메소드 통해서 현재 OAuth 인증을 진행 중이었는지 아닌지 확인
                OAuth2AuthorizationRequest authorizationRequest =
                        authorizationRequestRepository.loadAuthorizationRequest(request);

                if (authorizationRequest != null) {
                    // 소셜 로그인 종류 확인
                    String registrationId = authorizationRequest.getAttribute("registration_id");

                    // 쿠키 삭제
                    authorizationRequestRepository.removeAuthorizationRequest(request, response);

                    // 프론트엔드로 리다이렉트 시키면서 에러 사유를 전송 가능
                    String frontendRedirectUrl = "http://localhost:5173/login?error=cancel&provider=" + registrationId;
                    response.sendRedirect(frontendRedirectUrl);

                    return; // 다음 필터 안타게 여기서 return
                }
            }
        }

        // 에러가 아니거나 콜백 URL이 아니면 정상적으로 다음 필터(성공 흐름) 진행
        filterChain.doFilter(request, response);
    }
}