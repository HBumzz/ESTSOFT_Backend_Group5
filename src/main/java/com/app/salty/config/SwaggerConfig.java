package com.app.salty.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// localhost:8080/swagger-ui/index.html

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("EST SOFT BE[6] TEAM PROJECT #01 _5조_'Salty'") // API 제목
                .description("5조의 project_Salty API 문서.") // API 설명
                .version("1.0.0"); // 프로젝트 배포할 때 최초 API 버전
    }

}
