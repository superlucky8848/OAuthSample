package net.superluckyworks.oauthsample.auth_server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;
import net.superluckyworks.oauthsample.auth_server.service.MapOAuthUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration 
{
    @Autowired 
    MapOAuthUserService mapOAuthUserService;

    @Bean
    @Order(1)
    SecurityFilterChain authserverFilterChain(HttpSecurity http) throws Exception
    {
        OAuth2AuthorizationServerConfigurer oauth2AuthorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        http.securityMatcher(oauth2AuthorizationServerConfigurer.getEndpointsMatcher())
            .with(oauth2AuthorizationServerConfigurer, authserver -> authserver.oidc(Customizer.withDefaults()))
            .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .formLogin(Customizer.withDefaults());
        
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
        http.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize ->authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()
                .requestMatchers("/", "/index.html", "/register.html").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login -> oauth2Login
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(mapOAuthUserService)
                )
                .failureHandler((req, res, e) -> {
                    if(e instanceof OAuth2AuthenticationException)
                    {
                        OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) e;
                        if(oauth2Exception.getError().getErrorCode().compareTo("NO_LOCAL_USER") == 0)
                        {
                            res.sendRedirect("/register.html?email=" + oauth2Exception.getError().getDescription());
                        }
                        else res.sendRedirect("/login?error");
                    }
                    else res.sendRedirect("/login?error");
                })
            )
            .formLogin(formLogin -> formLogin
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}