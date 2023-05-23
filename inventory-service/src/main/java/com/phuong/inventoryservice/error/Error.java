package com.phuong.inventoryservice.error;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class Error extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity <Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.out.println("Error");
        Map <String, String> map = new LinkedHashMap<>();
        map.put ("Error", "Error for sure");
        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

}
