package com.lsm.backend.model;

import lombok.Getter;

@Getter
public enum  AuthProvider {
    local,
    NAVER,
    GOOGLE,
    KAKAO
}