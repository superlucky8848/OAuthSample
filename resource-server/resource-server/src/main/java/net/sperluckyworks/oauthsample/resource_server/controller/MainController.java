package net.sperluckyworks.oauthsample.resource_server.controller;

import org.springframework.web.bind.annotation.RestController;

import net.sperluckyworks.oauthsample.resource_server.model.ResultEntity;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class MainController 
{
    @GetMapping("/hello")    
    public ResultEntity<String> hello(
        @RequestParam(defaultValue = "World")
        String name)
    {
        String result = String.format("Hello %s!", name);
        return ResultEntity.from(result);
    }
}
