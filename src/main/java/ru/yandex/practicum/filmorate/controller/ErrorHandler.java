package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class
ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final ValidationException e, HttpServletRequest request) {
        return new ErrorResponse(e.getMessage(), request.getServletPath());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectParameterException(final UserNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(e.getMessage(), request.getServletPath());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectParameterException(final FilmNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(e.getMessage(), request.getServletPath());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectParameterException (final GenreNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(e.getMessage(), request.getServletPath());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectParameterException (final MpaNotFoundException e, HttpServletRequest request) {
        return new ErrorResponse(e.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleValidationException(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
             String fieldName = ((FieldError) error).getField();
             String errorMessage = error.getDefaultMessage();
             errors.put(fieldName, errorMessage);
         });
        return ResponseEntity.badRequest().body(errors);
    }
}
