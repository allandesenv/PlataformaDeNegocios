package com.suaempresa.auth.service;

import com.suaempresa.auth.exception.EmailAlreadyExistsException;
import com.suaempresa.auth.model.AuthResponse;
import com.suaempresa.auth.model.RegisterRequest;
import com.suaempresa.auth.model.User;
import com.suaempresa.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final JwtService jwtService; // Será injetado quando criarmos o JwtService

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        // this.jwtService = jwtService;
    }

    @Override
    @Transactional // Garante que a operação seja atômica
    public AuthResponse registerUser(RegisterRequest request) {
        // Cenário 2: E-mail já cadastrado.
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("E-mail já cadastrado");
        }

        // TODO: Adicionar validação de confirmação de senha no controller ou em um serviço de validação
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        // Cenário 1: Cadastro com e-mail e senha.
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Criptografa a senha
                .googleAuth(false) // Indica que não foi cadastro via Google
                .build();

        userRepository.save(newUser);

        // TODO: Gerar JWT e retornar no AuthResponse (depois de implementar JwtService)
        // String jwt = jwtService.generateToken(newUser);
        // return AuthResponse.builder().token(jwt).email(newUser.getEmail()).build();

        // Por enquanto, apenas um placeholder para o cenário de sucesso
        return AuthResponse.builder().token("temp-token-cadastro-sucesso").email(newUser.getEmail()).build();
    }
}