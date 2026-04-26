package ru.yandex.practicum.catsgram.exception;

public class InternalServerExeption extends RuntimeException {
    public InternalServerExeption(String message) {
        super(message);
    }
}
