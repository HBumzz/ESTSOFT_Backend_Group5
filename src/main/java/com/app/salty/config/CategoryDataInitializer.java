package com.app.salty.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class CategoryDataInitializer {

    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCategories() {
        transactionTemplate.execute(status -> {
            Long categoryCount = entityManager.createQuery(
                            "SELECT COUNT(c) FROM Category c", Long.class)
                    .getSingleResult();

            if (categoryCount == 0) {
                entityManager.createNativeQuery("""
                    INSERT INTO category (category_type, category_description) VALUES
                    ('FOOD', '식비 절약'),
                    ('TRANSPORT', '교통비 절약'),
                    ('SNACK', '간식비 절약'),
                    ('ETC', '기타 절약')
                    """).executeUpdate();
            }
            return null;
        });
    }
}
