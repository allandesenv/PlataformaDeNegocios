package com.suaempresa.auth.controller;

import com.suaempresa.auth.exception.EmailAlreadyExistsException;
import com.suaempresa.auth.model.AuthResponse;
import com.suaempresa.auth.model.LoginRequest; // NOVO IMPORT
import com.suaempresa.auth.model.RegisterRequest;
import com.suaempresa.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError; // Para MethodArgumentNotValidException
import org.springframework.web.bind.MethodArgumentNotValidException; // Para tratar erros de validação
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            // Este caso deve retornar BAD_REQUEST (400)
            // Lançar IllegalArgumentException para ser capturada pelo GlobalExceptionHandler
            throw new IllegalArgumentException("As senhas não coincidem.");
        }
        AuthResponse response = authService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login") // NOVO ENDPOINT DE LOGIN
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response); // Retorna 200 OK com o token (ou temp-token)
    }
}

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // Retorna 409 CONFLICT
    }

    @ExceptionHandler(IllegalArgumentException.class) // Para "As senhas não coincidem" ou "Credenciais inválidas"
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Retorna 400 BAD REQUEST
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Para erros de validação (@Valid)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Retorna 400 BAD REQUEST
    }
}