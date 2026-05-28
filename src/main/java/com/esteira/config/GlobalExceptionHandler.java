package com.esteira.config;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,String>> notFound(IllegalArgumentException e)   { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro",e.getMessage())); }
    @ExceptionHandler(IllegalStateException.class)    public ResponseEntity<Map<String,String>> badRequest(IllegalStateException e)    { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro",e.getMessage())); }
    @ExceptionHandler(Exception.class)                public ResponseEntity<Map<String,String>> generic(Exception e)                   { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro","Erro interno: "+e.getMessage())); }
}
