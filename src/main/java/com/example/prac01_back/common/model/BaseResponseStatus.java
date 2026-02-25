package com.example.prac01_back.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {
    //2000 번대 성공
    SUCCESS(true, 2000, "요청에 성공했습니다."),

    // 3000번 대 클라이언트 입력 오류, 입력값  검증 오류
    JWT_EXPIRED(false, 3001, "JWT 토큰이 만료되었습니다."),
    JWT_INVALID(false, 3002, "JWT 토큰이 유효하지 않습니다."),
    SIGNUP_DUPLICATE_EMAIL(false, 3003, "중복된 이메일입니다."),
    SIGNUP_INVALID_EMAIL(false, 3004, "비밀번호를 음 다양하게 해주십쇼."),

    // 5000번 때 실패
    FAIL(false, 5000, "요청에 실패했습니다.");

    private final boolean success;
    private final int code;
    private final String message;
}
