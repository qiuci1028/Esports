package com.esports.bigdata.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 3 配置
 * 访问地址：http://localhost:8080/api/doc.html
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Riot 电竞大数据分析平台 API")
                        .version("1.0.0")
                        .description("毕设项目后端：包含 Dashboard、Player、Champion、Match、Auth 五大模块")
                        .contact(new Contact().name("zjb").email("zjb@esports.com"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .schemaRequirement("X-Token", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Token"))
                .addSecurityItem(new SecurityRequirement().addList("X-Token"));
    }
}
