package com.example.demo.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    SUCCESS(true, 2000, "요청이 성공했습니다"),

    JWT_EXPIRED(false, 3001, "JWT 토큰이 만료되었습니다."),
    JWT_INVALID(false, 3002, "JWT 토큰이 유효하지 않습니다."),
    SIGNUP_DUPLICATE_EMAIL(false, 3003, "중복된 이메일입니다."),
    SIGNUP_INVALUD(false, 3004, "중복된 이메일입니다.");

    private final boolean success;
    private final int code;
    private final String message;
}
