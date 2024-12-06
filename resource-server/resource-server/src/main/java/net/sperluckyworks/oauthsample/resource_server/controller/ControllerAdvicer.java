package net.sperluckyworks.oauthsample.resource_server.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.sperluckyworks.oauthsample.resource_server.model.ApiException;
import net.sperluckyworks.oauthsample.resource_server.model.ResultEntity;

@RestControllerAdvice(basePackages = "net.sperluckyworks.oauthsample.resource_server.controller")
public class ControllerAdvicer 
{
    @ExceptionHandler(ApiException.class)
    ResultEntity<?> handleExcetpiton(ApiException exception)
    {
        if(exception == null) return ResultEntity.fail("Empty ApiException", "(NULL)", null);
        else return ResultEntity.fail(exception.getName(), exception);
    }

    @ExceptionHandler(IOException.class)
    ResultEntity<?> handleExcetpiton(IOException exception)
    {
        if(exception == null) return ResultEntity.fail("Empty IOException", "(NULL)", null);
        else return ResultEntity.fail(exception);
    }

    @ExceptionHandler(RuntimeException.class)
    ResultEntity<?> handleExcetpiton(RuntimeException exception)
    {
        if(exception == null) return ResultEntity.fail("Empty RuntimeException", "(NULL)", null);
        else return ResultEntity.fail(exception);
    }
}
