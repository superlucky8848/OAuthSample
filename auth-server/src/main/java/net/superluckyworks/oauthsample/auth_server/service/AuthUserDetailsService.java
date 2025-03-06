package net.superluckyworks.oauthsample.auth_server.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService
{
    private InMemoryUserDetailsManager userDetailsManager;

    public AuthUserDetailsService()
    {
        // Load some test users;
        List<UserDetails> users = Arrays.asList(
            User.withUsername("test")
                .password("{noop}test")
                .roles("USER")
                .build()
            ,User.withUsername("admin")
                .password("{noop}admin")
                .roles("USER", "ADMIN")
                .build()
            ,User.withUsername("mail.superlucky@gmail.com")
                .password("{noop}superlucky")
                .roles("USER", "ADMIN")
                .build()
        );

        userDetailsManager = new InMemoryUserDetailsManager(users);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        return userDetailsManager.loadUserByUsername(username);
    }
}
