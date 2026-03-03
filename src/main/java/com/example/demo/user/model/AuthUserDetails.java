package com.example.demo.user.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@Getter
@Builder
// 성공 했을때 반환하는 DTO
public class AuthUserDetails implements UserDetails, OAuth2User {
    private Long idx;
    private String username;
    private String password;
    private boolean enable;
    private String role;
    private String name;
    private Map<String, Object> attributes;

    public static AuthUserDetails from(User entity) {
        return AuthUserDetails.builder()
                .idx(entity.getIdx())
                .username(entity.getEmail())
                .name(entity.getName())
                .password(entity.getPassword())
                .enable(entity.isEnable())
                .role(entity.getRole())
                .build();
    }

    // attributes를 바로 받아와서 세팅하기
    public static AuthUserDetails from(String email, String name, String provider) {
        return AuthUserDetails.builder()
                .username(email)
                .name(name)
                .password(provider)
                .enable(true)
                .role("ROLE_USER")
                .build();
    }


    //-------<OAuth2User>----------
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    // --------------------------

    @Override
    public boolean isEnabled() {
        return enable;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
