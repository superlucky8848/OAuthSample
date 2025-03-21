package net.superluckyworks.oauthsample.resource_server.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import net.superluckyworks.oauthsample.resource_server.model.ResultEntity;

@RestController
@RequestMapping(path = "/api/public", produces = {MediaType.APPLICATION_JSON_VALUE})
public class PublicController 
{
    @Operation(description = "Say Hello Public Call")
    @GetMapping("/hello")    
    public ResultEntity<String> hello(
        @Parameter(description = "Say hello to whom?")
        @RequestParam(defaultValue = "World")
        String name)
    {
        String result = String.format("(Public) Hello %s!", name);
        return ResultEntity.success(result);
    }
}
