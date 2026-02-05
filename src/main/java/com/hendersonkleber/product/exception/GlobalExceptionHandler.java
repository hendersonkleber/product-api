package com.hendersonkleber.product.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception, WebRequest request) {
        var status = HttpStatus.CONFLICT;
        var problem = ProblemDetail.forStatus(status);

        problem.setTitle(status.name());
        problem.setDetail(exception.getMessage());
        problem.setProperty("timestamp", Instant.now());

        logger.error(
                "Exception occurred: {}, Request Details: {}",
                exception.getMessage(),
                request.getDescription(false),
                exception
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var problem = ProblemDetail.forStatus(status);

        problem.setTitle(status.name());
        problem.setDetail(exception.getMessage());
        problem.setProperty("timestamp", Instant.now());

        logger.error(
                "Exception occurred: {}, Request Details: {}",
                exception.getMessage(),
                request.getDescription(false),
                exception
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var problem = ProblemDetail.forStatus(status);

        List<String> errors = new ArrayList<>();

        for (var error: exception.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        for (var error: exception.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        problem.setTitle(status.name());
        problem.setDetail("Invalid argument");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errors", errors);

        logger.error(
                "Exception occurred: {}, Request Details: {}",
                exception.getMessage(),
                request.getDescription(false),
                exception
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception exception, WebRequest request) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var problem = ProblemDetail.forStatus(status);

        problem.setTitle(status.name());
        problem.setDetail(exception.getMessage());
        problem.setProperty("timestamp", Instant.now());

        logger.error("Exception occurred: {}, Request Details: {}",
                exception.getMessage(),
                request.getDescription(false),
                exception
        );

        return ResponseEntity.status(status).body(problem);
    }
}
