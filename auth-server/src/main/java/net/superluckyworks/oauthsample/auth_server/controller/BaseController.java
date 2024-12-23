package net.superluckyworks.oauthsample.auth_server.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
}
