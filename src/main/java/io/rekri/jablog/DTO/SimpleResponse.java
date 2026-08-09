package io.rekri.jablog.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Стандарт для всех http ответов. Все классы ответов должны наследоваться от этого класса.
 * */
@Getter
@Setter
abstract public class SimpleResponse {
    private int status;
    private String message;
    private final Instant timestamp = Instant.now();
}
