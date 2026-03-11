package com.example.demo.user;

import com.example.demo.common.exception.BaseException;
import com.example.demo.user.model.User;
import com.example.demo.user.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;

import static com.example.demo.common.model.BaseResponseStatus.SIGNUP_DUPLICATE_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private EmailVerifyRepository emailVerifyRepository;

    @InjectMocks
    private UserService userService;

    UserDto.SignupReq dto;
    User user;

    @BeforeEach
    void setUp() {
        dto = new UserDto.SignupReq(
                "test01@test.com", "test01", "qwer1234"
        );

        user = User.builder()
                .idx(1L)
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }


    @Test
    void userService_회원가입_성공() {
        // given
        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserDto.SignupRes result = userService.signup(dto);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getIdx()); // 성공
        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void userService_회원가입_실패_중복된_이메일() {
        // given
        given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(user));

        // when
        BaseException ex = assertThrows(BaseException.class,
                () -> userService.signup(dto));

        // then
        assertEquals(ex.getStatus().getCode(), SIGNUP_DUPLICATE_EMAIL.getCode());
        assertEquals(ex.getMessage(), SIGNUP_DUPLICATE_EMAIL.getMessage());
    }
}