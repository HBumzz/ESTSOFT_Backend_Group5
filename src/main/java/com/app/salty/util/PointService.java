package com.app.salty.util;

import com.app.salty.user.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PointService {

    public void addPoint(Users user, Long point) {
        log.info("Before adding point - User: {}, Current point: {}, Adding: {}",
                user.getId(), user.getPoint(), point);
        user.addPoint(point);
        log.info("After adding point - New total: {}", user.getPoint());
    }
}
