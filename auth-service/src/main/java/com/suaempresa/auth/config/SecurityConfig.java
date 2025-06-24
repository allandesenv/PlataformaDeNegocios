package com.suaempresa.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita a segurança web do Spring Security
public class SecurityConfig {

    // Define um PasswordEncoder para criptografar senhas (usaremos BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração da cadeia de filtros de segurança HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para APIs REST
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/oauth2/**").permitAll() // Permite acesso público a endpoints de autenticação
                        .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
                );
        // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Para APIs REST, sem estado de sessão
        // .authenticationProvider(authenticationProvider()); // Será adicionado quando criarmos o AuthenticationProvider
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Será adicionado para filtro JWT

        return http.build();
    }

    // TODO: Adicionar AuthenticationProvider e JwtAuthFilter mais tarde
}