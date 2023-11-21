package com.lsm.backend.config;
import com.lsm.backend.security.CustomUserDetailsService;
import com.lsm.backend.security.RestAuthenticationEntryPoint;
import com.lsm.backend.security.TokenAuthenticationFilter;
import com.lsm.backend.security.oauth2.CustomOAuth2UserService;
import com.lsm.backend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.lsm.backend.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.lsm.backend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
//    @Bean
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/",
                                        "/login/**",
                                        "/login/**",
                                        "/error",
                                        "/favicon.ico",
                                        "/**/*.png",
                                        "/**/*.gif",
                                        "/**/*.svg",
                                        "/**/*.jpg",
                                        "/**/*.html",
                                        "/**/*.css",
                                        "/**/*.js")
                                .permitAll()
                                .requestMatchers("/auth/**", "/oauth2/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .authorizationEndpoint(authorizationEndpoint ->
                                        authorizationEndpoint
                                                .baseUri("/oauth2/authorize")
                                                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                                )
                                .redirectionEndpoint(redirectionEndpoint ->
                                        redirectionEndpoint
                                                .baseUri("/login/oauth2/code/*")
                                )
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint
                                                .userService(customOAuth2UserService)
                                )
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new RestAuthenticationEntryPoint()));
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
