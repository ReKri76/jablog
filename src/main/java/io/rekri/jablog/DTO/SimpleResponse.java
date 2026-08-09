package io.rekri.jablog.DTO;

import lombok.Data;

import java.time.Instant;

/**
 * Стандарт для всех http ответов. Все классы ответов должны наследоваться от этого класса.
 * */
@Data
abstract public class SimpleResponse {
    private int status;
    private String message;
    private final Instant timestamp = Instant.now();
}
