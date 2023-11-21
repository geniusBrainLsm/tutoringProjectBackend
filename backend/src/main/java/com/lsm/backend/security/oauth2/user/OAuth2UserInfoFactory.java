package com.lsm.backend.security.oauth2.user;
import com.lsm.backend.security.oauth2.user.GoogleOAuth2UserInfo;
import com.lsm.backend.security.oauth2.user.KakaoOAuth2UserInfo;
import com.lsm.backend.security.oauth2.user.NaverOAuth2UserInfo;
import com.lsm.backend.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        switch (authProvider) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}