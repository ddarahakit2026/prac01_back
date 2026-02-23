package org.example.spring03.user;

import lombok.RequiredArgsConstructor;
import org.example.spring03.user.model.AuthUserDetails;
import org.example.spring03.user.model.UserDto;
import org.example.spring03.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @GetMapping("/verify")
    public ResponseEntity verify(String uuid) {

        userService.verify(uuid);

        return ResponseEntity.ok("asd");
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserDto.SignupReq dto) {
        userService.signup(dto);

        return ResponseEntity.ok("성공");
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDto.LoginReq dto) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword(), null);

        Authentication authentication = authenticationManager.authenticate(token);
        AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();

        if(user != null) {
            String jwt = JwtUtil.createToken(user.getIdx(), user.getUsername(), user.getRole());
            return ResponseEntity.ok().header("Set-Cookie", "ATOKEN=" + jwt + "; Path=/").build();
        }

        return ResponseEntity.ok("로그인 실패");

    }


}
