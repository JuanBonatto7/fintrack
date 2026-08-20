package com.juanbonatto.fintrack.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Email o contraseña inválidos");
    }
}
