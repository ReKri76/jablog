package io.rekri.jablog.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class InvalidRulesException extends RuntimeException {
    public InvalidRulesException(String message) {
        super(message);
    }
}
