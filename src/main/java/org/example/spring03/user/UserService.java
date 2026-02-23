package org.example.spring03.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.spring03.user.model.AuthUserDetails;
import org.example.spring03.user.model.EmailVerify;
import org.example.spring03.user.model.User;
import org.example.spring03.user.model.UserDto;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final EmailVerifyRepository emailVerifyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public void signup(UserDto.SignupReq dto) {
        User user = dto.toEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        // 이메일 전송
        MimeMessage message  = mailSender.createMimeMessage();
        try {
            String uuid = UUID.randomUUID().toString();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(dto.getEmail());
            String subject = "[안녕] 환영";
            String htmlContents = "<a href='http://localhost:8080/user/verify?uuid="+uuid+"'>이메일 인증</a>";
            helper.setSubject(subject);
            helper.setText(htmlContents, true);
            mailSender.send(message);
            EmailVerify emailVerify = EmailVerify.builder().email(dto.getEmail()).uuid(uuid).build();
            emailVerifyRepository.save(emailVerify);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }



    }


    // 시큐리티랑 상관 없는 내 서비스
    public UserDetails login(UserDto.LoginReq dto) throws UsernameNotFoundException {
        System.out.println("UserService 실행됨");

        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow();


        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            // 로그인 성공
        }

        if(user.isEnable()) {
            // 활성 상태 확인
        }



        return AuthUserDetails.from(user);
    }



    // TODO : 5번
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("UserService 실행됨");

        // TODO : 6번
        User user = userRepository.findByEmail(username).orElseThrow();


        // TODO : 7번
        return AuthUserDetails.from(user);
    }

    public void verify(String uuid) {
        EmailVerify emailVerify = emailVerifyRepository.findByUuid(uuid).orElseThrow();
        User user = userRepository.findByEmail(emailVerify.getEmail()).orElseThrow();
        user.setEnable(true);
        userRepository.save(user);
    }
}
