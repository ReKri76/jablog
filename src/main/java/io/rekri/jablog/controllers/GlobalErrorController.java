package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.SimpleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalErrorController{

    public static class BadCredentialsResponse extends SimpleResponse{}

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BadCredentialsResponse> badCredentialsHandler(BadCredentialsException e){

        BadCredentialsResponse res = new BadCredentialsResponse();
        res.setStatus(401);
        res.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(res);
    }
}