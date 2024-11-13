package com.app.salty.config;

import com.app.salty.config.filter.JwtAuthenticationFilter;
import com.app.salty.config.filter.LoginFilter;
import com.app.salty.user.service.CustomUserDetailsService;
import com.app.salty.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug(" WebSecurityConfig Start !!! ");

//        LoginFilter loginFilter = new LoginFilter(
//                authenticationManager(authenticationConfiguration),
//                jwtUtil
//        );

//        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
//                jwtUtil,
//                customUserDetailsService
//        );

        return http
                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
//                                "/h2-console/**" //임시
                        ).permitAll()
                          .requestMatchers("/api/auth/login,","/auth/signup"
                                ,"/auth/login","/auth/signup"
                           ).anonymous()
                        .requestMatchers("/chboard/**").hasRole("USER4")
                        .requestMatchers("/api/boards/**","/board/**").hasRole("USER2")
                        .requestMatchers("/checklist/**", "/api/checklists/**").hasRole("USER3")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/ws/chat/**").permitAll()
                        .anyRequest().permitAll()
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(authorization -> authorization
//                                .baseUri("/oauth2/authorization"))
//                        .redirectionEndpoint(redirection -> redirection
//                                .baseUri("/api/auth/**/callback"))
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(())
//                        .successHandler(oAuth2AuthenticationSuccessHandler)
//                        .failureHandler(oAuth2AuthenticationFailureHandler)
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterAt(loginFilter, JwtAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/auth/login")
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring();
    }
}
