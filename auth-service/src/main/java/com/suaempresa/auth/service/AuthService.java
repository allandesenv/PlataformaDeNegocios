package com.suaempresa.auth.service;

import com.suaempresa.auth.model.AuthResponse;
import com.suaempresa.auth.model.RegisterRequest;

public interface AuthService {
    AuthResponse registerUser(RegisterRequest request);
    // AuthResponse loginUser(LoginRequest request); // Será adicionado para login
    // AuthResponse registerOrLoginGoogleUser(String googleEmail, String googleName); // Para Google OAuth
}