package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FilmNotFoundException extends ResponseStatusException {

    private final String path;

    public FilmNotFoundException(String message, String path) {
        super(HttpStatus.valueOf(message));
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
