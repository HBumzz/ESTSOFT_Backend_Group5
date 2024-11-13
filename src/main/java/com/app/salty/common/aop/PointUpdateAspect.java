package com.app.salty.common.aop;

import com.app.salty.user.common.Role;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.CustomUserDetailsService;
import com.app.salty.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PointUpdateAspect {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final HttpSession httpSession;

    @AfterReturning(
            pointcut ="execution(* com.app.salty.util.PointService.addPoint(..)) && args(user, point)"
            ,argNames = "joinPoint,user,point"
    )
    public void afterPointUpdate(JoinPoint joinPoint, Users user, Long point) {
        System.out.println("+++++++++++++=AOP 작동 확인+++++++++++++");
        Long currentPoint = user.getPoint();
        log.info("Current point : {} ++++++++++++++", currentPoint);

        if (point == 100L) {
            userService.addUserRole(user, Role.USER2);
            updatePrincipal(user);
            System.out.println("권한 업데이트 완료");
        } else if (point == 300L) {
            userService.addUserRole(user, Role.USER3);
            updatePrincipal(user);
        } else if (point == 500L) {
            userService.addUserRole(user, Role.USER4);
            updatePrincipal(user);
        }

    }

    public void updatePrincipal(Users user) {
        // 새로운 UserDetails 생성
        UserDetails newUserDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        log.info("New Authorities: {}", newUserDetails.getAuthorities());

        // 현재 Authentication 가져오기
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        // 새로운 Authentication 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                currentAuth.getCredentials(),  // 기존 credentials 유지
                newUserDetails.getAuthorities()
        );

        // SecurityContext 업데이트
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(newAuth);
        SecurityContextHolder.setContext(context);

        // 세션에 SecurityContext 업데이트
        HttpSession session = httpSession;
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        log.info("Security Context 업데이트 완료. 현재 권한: {}",
                newAuth.getAuthorities());

    }

}
