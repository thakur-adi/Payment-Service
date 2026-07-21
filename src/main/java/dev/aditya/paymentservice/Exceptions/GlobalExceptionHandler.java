package dev.aditya.paymentservice.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomStripeException.class)
    public ResponseEntity<String> handleRunTimeException(CustomStripeException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
    }

}
