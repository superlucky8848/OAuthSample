package net.superluckyworks.oauthsample.auth_server.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration 
{
    @Bean
    PasswordEncoder passwordEncoder() 
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService testUserDetailsService() 
    {
        PasswordEncoder testPasswordEncoder = passwordEncoder();

        UserDetails testUser = User.withUsername("test")
            .password(testPasswordEncoder.encode("test"))
            .roles("USER")
            .build();

        UserDetails testAdmin = User.withUsername("admin")
            .password(testPasswordEncoder.encode("admin"))
            .roles("USER", "ADMIN")
            .build();

        UserDetailsService result = new InMemoryUserDetailsManager(Arrays.asList(testUser, testAdmin));

        return result;
    }
}