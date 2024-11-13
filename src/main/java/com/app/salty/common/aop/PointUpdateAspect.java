package com.app.salty.common.aop;

import com.app.salty.user.common.Role;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
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

    @AfterReturning(
            pointcut ="execution(* com.app.salty.user.entity.Users.addPoint(..)) "
    )
    public void afterPointUpdate(JoinPoint joinPoint) {
        System.out.println("+++++++++++++=AOP 작동 확인+++++++++++++");
        Users user = (Users) joinPoint.getTarget();
        Long point = user.getPoint();
        log.info(
                "point : {} ++++++++++++++",point
        );
        if (point == 100L) {
            userService.addUserRole(user, Role.USER2);
            System.out.println("권한 업데이트 완료");
        } else if (point == 500L) {
            userService.addUserRole(user, Role.USER3);
        }

    }

}
