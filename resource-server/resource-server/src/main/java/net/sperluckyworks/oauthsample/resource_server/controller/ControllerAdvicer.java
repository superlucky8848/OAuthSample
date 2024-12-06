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
        if(exception == null) return ResultEntity.fromError("Empty ApiException", "(NULL)", null);
        else return ResultEntity.fromThrowable(exception.getName(), exception);
    }

    @ExceptionHandler(IOException.class)
    ResultEntity<?> handleExcetpiton(IOException exception)
    {
        if(exception == null) return ResultEntity.fromError("Empty IOException", "(NULL)", null);
        else return ResultEntity.fromThrowable(exception);
    }

    @ExceptionHandler(RuntimeException.class)
    ResultEntity<?> handleExcetpiton(RuntimeException exception)
    {
        if(exception == null) return ResultEntity.fromError("Empty RuntimeException", "(NULL)", null);
        else return ResultEntity.fromThrowable(exception);
    }
}
