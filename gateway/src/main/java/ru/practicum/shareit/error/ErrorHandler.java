package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleThrowable(final Throwable e) {
        log.warn(e.getMessage(), e.getCause());
        return new ResponseEntity<>(new ErrorResponse("Неожиданная ошибка.", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage(), e.getCause());
        return new ResponseEntity<>(new ErrorResponse("Не корректно(-ы)е поле(-я) пользователя.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIncorrectStatusException(final IncorrectStatusException e) {
        log.warn(e.getMessage(), e.getCause());
        return new ResponseEntity<>(new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
