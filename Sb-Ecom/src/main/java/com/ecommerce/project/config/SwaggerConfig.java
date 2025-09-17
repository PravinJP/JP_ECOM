package com.ecommerce.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description =
                "A JWT token is required to access this API. A valid JWT can be obtained by logging in to HT Oauth server.")
@OpenAPIDefinition(
        info = @Info(title = "Review V3 API Documentation", version = "1.0"),
        security = @SecurityRequirement(name = "Bearer Authentication"))
public class SwaggerConfig {

    public static final String TAG_REVIEW_V3_API = "v1";
    public static final String TITLE = "E-commerce";
    public static final String DESCRIPTION = "E-commerce application.";
    public static final String VERSION = "1.0";

    @Bean
    public GroupedOpenApi landingPageApiV1() {
        return GroupedOpenApi.builder()
                .group("Ecommerce")
                .addOpenApiCustomizer(
                        openApi -> {
                            openApi.getInfo().version(VERSION).title(TITLE).description(DESCRIPTION);
                            openApi.addServersItem(
                                    new io.swagger.v3.oas.models.servers.Server().description("Default Server URL"));
                        })
                .build();
    }
}
