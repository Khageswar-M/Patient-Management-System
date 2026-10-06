package com.pm.patientservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // This exception handler used to handle all the errors related to the
    // method argument based
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex
    ){
        var errors = new HashMap<String, String>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                    error -> errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    )
                );
        return ResponseEntity.badRequest().body(errors);
    }
}
