package net.superluckyworks.oauthsample.auth_server.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.superluckyworks.oauthsample.auth_server.model.HybridUser;

@Service
public class AuthUserDetailsService implements UserDetailsService
{
    Map<String, HybridUser> users = new HashMap<>();

    public AuthUserDetailsService()
    {
        users.put("test", 
            new HybridUser(
                User.withUsername("test")
                    .password("{noop}test")
                    .roles("USER")
                    .build()
            )
        );
        users.put("admin", 
            new HybridUser(
                User.withUsername("admin")
                    .password("{noop}admin")
                    .roles("USER", "ADMIN")
                    .build()
            )
        );
        users.put("mail.superlucky@gmail.com", 
            new HybridUser(
                User.withUsername("mail.superlucky@gmail.com")
                    .password("{noop}superlucky")
                    .roles("USER", "ADMIN")
                    .build()
            )
        );
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        if(users.containsKey(username))
        {
            return users.get(username);
        }
        else
        {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
