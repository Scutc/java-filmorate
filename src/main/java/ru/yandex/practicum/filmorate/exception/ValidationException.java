package ru.yandex.practicum.filmorate.exception;

public class ValidationException  extends RuntimeException {

    private final String path;

    public ValidationException(String message, String path) {
        super(message);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
