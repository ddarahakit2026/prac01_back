package com.example.demo.user;

import com.example.demo.user.model.AuthUserDetails;
import com.example.demo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("서비스 코드 실행");

        // 어떤걸로 로그인을 했는지 받아 올 수 있다.
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 카카오랑 통신후 응답
        //OAuuth2 로그인 설정 , properties로 들어가 있음
        // 받아온거면 무조건 성공 로직이다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = ((Long)attributes.get("id")).toString(); // 카카오 회원 번호
        System.out.println(providerId);

        String email = providerId + "@kakao.social";
        Map properties = (Map) attributes.get("properties");
        String name = (String) properties.get("nickname");

        // DB에 회원이 있나 없나 확인
        Optional<User> result = userRepository.findByEmail(email);


        // 없으면 가입 시켜주기
        User user = null;
        if(!result.isPresent()){
           user = userRepository.save(
                    User.builder()
                            .email(email)
                            .name(name)
                            .password("kakao") // 필드를 만들어서 따로 나중에 뭘로 로그인 했어요 라고 알려주기도 한다.
                            .enable(true)
                            .role("ROLE_USER")
                            .build()
            );
            return AuthUserDetails.from(user);
        }
        else { // 있으면 해당 사용자 반환
          user = result.get();

            return AuthUserDetails.from(user);
        }

    }
}
