package net.superluckyworks.oauthsample.resource_server.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
@SecuritySchemes({
    @SecurityScheme(
        name = "basicScheme", 
        type = SecuritySchemeType.HTTP, 
        scheme = "basic"),
    @SecurityScheme(
        name = "oauth2Scheme",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = 
            @OAuthFlow(
                authorizationUrl = "http://localhost:8081/oauth2/authorize",
                tokenUrl = "http://localhost:8081/oauth2/token",
                scopes = {
                    @OAuthScope(name="openid"),
                    @OAuthScope(name="profile"),
                    @OAuthScope(name="email")
                }
            )
        )
    )
})

public class ApiDocConfiguration 
{
    @Bean
    public OpenAPI apiDefination()
    {
        return new OpenAPI()
            .info(
                new Info().title("OAuth Test API")
                .description("Test APIs for OAuth test project")
                .version("v1.0.1")
                .license(
                    new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                )
            );
    }

    @Bean
    public GroupedOpenApi publicApi()
    {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/public/**")
            .build();
    }

    @Bean
    public GroupedOpenApi privateApi()
    {
        return GroupedOpenApi.builder()
            .group("private")
            .pathsToMatch("/api/private/**")
            .build();
    }
}
