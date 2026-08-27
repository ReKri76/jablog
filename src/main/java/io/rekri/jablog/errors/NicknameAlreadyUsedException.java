package io.rekri.jablog.errors;

public class NicknameAlreadyUsedException extends RuntimeException {
    public NicknameAlreadyUsedException(String message) {
        super(message);
    }
}