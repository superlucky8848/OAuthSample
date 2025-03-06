package net.superluckyworks.oauthsample.auth_server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class BaseController 
{
    @PostMapping("/process")
    public String postProcess(@RequestBody String entity) 
    {    
        System.out.println("POST /process: " + entity);   
        return entity;
    }
    
    @DeleteMapping("/process")
    public String deleteProcess(@RequestBody String entity) 
    {
        System.out.println("DELETE /process: " + entity);
        return entity;
    }

    @GetMapping(path = "/user-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object userInfo(Authentication authentication)
    {
        Object principal = authentication.getPrincipal();

        if(principal instanceof OAuth2User)
        {
            OAuth2User oauth2User = (OAuth2User) principal;
            String email = (String) oauth2User.getAttribute("email");

            System.out.println("email: " + email);
            System.out.println("authorities: ");
            oauth2User.getAuthorities().forEach(System.out::println);
        }

        return principal;
    }

    @GetMapping(path = "/user-authorities", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GrantedAuthority> userAuthorities(Authentication authentication) 
    {
        return new ArrayList<GrantedAuthority>(authentication.getAuthorities());
    }
    
}
