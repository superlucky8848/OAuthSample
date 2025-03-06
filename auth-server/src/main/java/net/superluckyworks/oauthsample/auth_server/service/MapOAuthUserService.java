package net.superluckyworks.oauthsample.auth_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import net.superluckyworks.oauthsample.auth_server.model.HybridUser;

@Service
public class MapOAuthUserService extends DefaultOAuth2UserService
{
    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException 
    {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        try
        {
            UserDetails localUser = authUserDetailsService.loadUserByUsername(email);
            return new HybridUser(localUser, oAuth2User.getAttributes());
        }
        catch (UsernameNotFoundException e)
        {
            OAuth2Error oauth2Error = new OAuth2Error("NO_LOCAL_USER", email, null);
            throw new OAuth2AuthenticationException(oauth2Error, e); 
        }
    }
}