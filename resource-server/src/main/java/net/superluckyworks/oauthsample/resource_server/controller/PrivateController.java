package net.superluckyworks.oauthsample.resource_server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import net.superluckyworks.oauthsample.resource_server.model.ResultEntity;

@RestController
@RequestMapping(path = "/api/private", produces = { MediaType.APPLICATION_JSON_VALUE })
@SecurityRequirements({
    @SecurityRequirement(name = "basicScheme"),
    @SecurityRequirement(name = "oauth2Scheme")
})
public class PrivateController 
{
    @Operation(description = "Say Hello Private Call")
    @GetMapping("/hello")    
    public ResultEntity<String> hello(
        @Parameter(description = "Say hello to whom?")
        @RequestParam(defaultValue = "World")
        String name)
    {
        String result = String.format("(Private) Hello %s!", name);
        return ResultEntity.success(result);
    }

    @Operation(description = "Get Current User Information")
    @GetMapping("/user-info")
    public ResultEntity<Object> userInfo(Authentication auth)
    {
        return ResultEntity.success(auth.getPrincipal());
    }

    @Operation(description = "Get Current User Authorities")
    @GetMapping("/user-authorities")
    public ResultEntity<List<GrantedAuthority>> userAuthorities(Authentication auth) 
    {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>(auth.getAuthorities());
        return ResultEntity.success(result);
    }
}
