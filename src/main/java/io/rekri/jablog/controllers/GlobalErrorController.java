package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.SimpleResponse;
import io.rekri.jablog.errors.InvalidRulesException;
import io.rekri.jablog.errors.NicknameAlreadyUsedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalErrorController{

    public static class ErrorResponse extends SimpleResponse{}

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsHandler(BadCredentialsException e){

        final ErrorResponse res = new ErrorResponse();
        res.setStatus(401);
        res.setMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());

        final ErrorResponse res = new ErrorResponse();
        res.setMessage("Invalid arguments");
        res.setStatus(400);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(res);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid arguments: {}", e.getMessage());

        final ErrorResponse res = new ErrorResponse();
        res.setMessage("Invalid arguments");
        res.setStatus(400);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(res);
    }

    @ExceptionHandler(InvalidRulesException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRulesException(InvalidRulesException e) {
        log.warn("Invalid rules of board: {}", e.getMessage());

        final ErrorResponse res = new ErrorResponse();
        res.setMessage(e.getMessage());
        res.setStatus(400);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(res);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        log.warn("ResponseStatusException: status ={}, message = {}", e.getStatusCode(), e.getReason());

        final ErrorResponse res = new ErrorResponse();
        res.setMessage(e.getReason());
        res.setStatus(e.getStatusCode().value());

        return ResponseEntity
                .status(e.getStatusCode())
                .body(res);
    }

    @ExceptionHandler(NicknameAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleNicknameAlreadyUsedException(NicknameAlreadyUsedException e) {
        log.warn("NicknameAlreadyUsedException:{}", e.getMessage());

        final ErrorResponse res = new ErrorResponse();
        res.setMessage("This nickname is already used.");
        res.setStatus(409);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(res);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e){

        final ErrorResponse res = new ErrorResponse();
        res.setStatus(500);
        res.setMessage("Internal server error.");

        log.error("Internal server error: ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(res);
    }
}