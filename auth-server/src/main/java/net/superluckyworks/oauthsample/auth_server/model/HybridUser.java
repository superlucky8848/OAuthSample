package net.superluckyworks.oauthsample.auth_server.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class HybridUser extends User implements OAuth2User
{
    private Map<String, Object> attributes = new HashMap<>();

    public HybridUser(UserDetails user)
    {
        super(
            user.getUsername(), 
            user.getPassword(), 
            user.isEnabled(), 
            user.isAccountNonExpired(), 
            user.isCredentialsNonExpired(), 
            user.isAccountNonLocked(), 
            user.getAuthorities());
    }

    public HybridUser(UserDetails user, Map<String, Object> attributes)
    {
        this(user);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() 
    {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }
    
}
