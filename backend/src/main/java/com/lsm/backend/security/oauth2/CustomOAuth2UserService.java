package com.lsm.backend.security.oauth2;

import com.lsm.backend.exception.OAuthProviderMissMatchException;
import com.lsm.backend.model.AuthProvider;
import com.lsm.backend.model.User;
import com.lsm.backend.repository.UserRepository;
import com.lsm.backend.security.UserPrincipal;
import com.lsm.backend.security.oauth2.user.OAuth2UserInfo;
import com.lsm.backend.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        AuthProvider authProvider = AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, user.getAttributes());
        Optional<User> optionalUser = userRepository.findByEmail(userInfo.getEmail());
        User savedUser = optionalUser.orElse(null);
        //TODO: 예외처리

        if (savedUser != null) {
            if (authProvider != savedUser.getAuthProvider()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + authProvider +
                                " account. Please use your " + savedUser.getAuthProvider() + " account to login."
                );
            }
            updateUser(savedUser, userInfo);
        } else {
            savedUser = createUser(userInfo, authProvider);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, AuthProvider authProvider) {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getEmail(),
                "Y",
                userInfo.getImageUrl(),
                authProvider,
                now,
                now
        );

        return userRepository.saveAndFlush(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getName().equals(userInfo.getName())) {
            user.setName(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && !user.getProfileImageUrl().equals(userInfo.getImageUrl())) {
            user.setProfileImageUrl(userInfo.getImageUrl());
        }

        return user;
    }
}