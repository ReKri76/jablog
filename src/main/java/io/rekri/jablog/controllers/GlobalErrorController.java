package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.SimpleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalErrorController{

    public static class ErrorResponse extends SimpleResponse{}

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsHandler(BadCredentialsException e){

        ErrorResponse res = new ErrorResponse();
        res.setStatus(401);
        res.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());

        ErrorResponse res = new ErrorResponse();
        res.setMessage("Invalid arguments");
        res.setStatus(400);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(res);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ErrorResponse> handleIllegalAccessException(IllegalAccessException e) {
        log.warn("Invalid arguments: {}", e.getMessage());

        ErrorResponse res = new ErrorResponse();
        res.setMessage("Invalid arguments");
        res.setStatus(400);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(res);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e){

        ErrorResponse res = new ErrorResponse();
        res.setStatus(500);
        res.setMessage("Internal server error.");

        log.error("Internal server error: ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(res);
    }
}