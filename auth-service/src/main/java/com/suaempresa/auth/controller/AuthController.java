package com.suaempresa.auth.controller;

import com.suaempresa.auth.exception.EmailAlreadyExistsException; // Importe a exceção
import com.suaempresa.auth.model.AuthResponse;
import com.suaempresa.auth.model.RegisterRequest;
import com.suaempresa.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ControllerAdvice; // NOVO IMPORT
import org.springframework.web.bind.annotation.ExceptionHandler; // NOVO IMPORT
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
        // Lógica de validação de senhas no controller, como já existe no seu código
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            // Este caso deve retornar BAD_REQUEST (400), não CONFLICT (409)
            // Podemos criar um DTO de erro mais genérico para isso ou usar um Map
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "As senhas não coincidem.");
            // Poderíamos retornar um ResponseEntity<Map<String, String>>
            return ResponseEntity.badRequest().body(AuthResponse.builder().email(request.getEmail()).token(null).build());
        }

        AuthResponse response = authService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

// Adicione esta nova classe para tratar exceções globalmente
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // Retorna 409 CONFLICT
    }

    // Você pode adicionar mais @ExceptionHandler para outras exceções comuns, ex:
    // @ExceptionHandler(MethodArgumentNotValidException.class) para erros de @Valid
    // @ExceptionHandler(IllegalArgumentException.class) para senhas que não coincidem (se não for tratada no controller)
    // public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    //     Map<String, String> errors = new HashMap<>();
    //     ex.getBindingResult().getAllErrors().forEach((error) -> {
    //         String fieldName = ((FieldError) error).getField();
    //         String errorMessage = error.getDefaultMessage();
    //         errors.put(fieldName, errorMessage);
    //     });
    //     return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    // }
}