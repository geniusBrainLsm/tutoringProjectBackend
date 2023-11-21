package com.lsm.backend.security.oauth2.user;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    /** naver 는 response 안에 담겨져있기 때문에 response 를 해준다 */
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("response"));
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image");
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }
}