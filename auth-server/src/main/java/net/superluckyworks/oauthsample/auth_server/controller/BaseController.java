package net.superluckyworks.oauthsample.auth_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



@RestController
public class BaseController 
{
    @Autowired
    ObjectMapper objectMapper;

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

    @GetMapping("/user-info")
    public String userInfo(@AuthenticationPrincipal OAuth2User oauth2User) throws JsonProcessingException 
    {
        return objectMapper.writeValueAsString(oauth2User);
    }
    
}
