package com.suaempresa.auth.service;

import com.suaempresa.auth.exception.EmailAlreadyExistsException;
import com.suaempresa.auth.model.AuthResponse;
import com.suaempresa.auth.model.LoginRequest; // NOVO IMPORT
import com.suaempresa.auth.model.RegisterRequest;
import com.suaempresa.auth.model.User;
import com.suaempresa.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager; // NOVO IMPORT
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // NOVO IMPORT
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; // NOVO IMPORT
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // NOVO: para autenticação de credenciais
    // private final JwtService jwtService; // Será injetado quando criarmos o JwtService

    // Atualize o construtor para incluir AuthenticationManager
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        // this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("E-mail já cadastrado");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .googleAuth(false)
                .build();

        userRepository.save(newUser);

        // TODO: Gerar JWT e retornar no AuthResponse (depois de implementar JwtService)
        // String jwt = jwtService.generateToken(newUser);
        // return AuthResponse.builder().token(jwt).email(newUser.getEmail()).build();

        return AuthResponse.builder().token("temp-token-cadastro-sucesso").email(newUser.getEmail()).build();
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        try {
            // Autentica as credenciais usando o Spring Security AuthenticationManager
            // Isso irá disparar UserDetailsService e PasswordEncoder para verificar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Se a autenticação foi bem-sucedida, podemos buscar o usuário
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação.")); // Isso não deveria acontecer

            // TODO: Gerar JWT aqui após a autenticação bem-sucedida
            // String jwt = jwtService.generateToken(user);
            // return AuthResponse.builder().token(jwt).email(user.getEmail()).build();

            return AuthResponse.builder().token("temp-token-login-sucesso").email(user.getEmail()).build();

        } catch (AuthenticationException e) {
            // Se a autenticação falhar (usuário não encontrado ou senha incorreta)
            throw new IllegalArgumentException("Credenciais inválidas"); // Cenário de credenciais inválidas
        }
    }
}