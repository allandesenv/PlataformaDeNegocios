package com.suaempresa.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Nome da tabela no banco de dados
@Data // Gera getters, setters, toString, equals e hashCode (Lombok)
@NoArgsConstructor // Gera construtor sem argumentos (Lombok)
@AllArgsConstructor // Gera construtor com todos os argumentos (Lombok)
@Builder // Permite construir objetos de forma fluente (Lombok)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID gerado automaticamente pelo banco de dados
    private Long id;

    @Column(nullable = false, unique = true) // E-mail é obrigatório e único
    private String email;

    @Column(nullable = false) // Senha é obrigatória
    private String password; // A senha será armazenada hashed

    private String name; // Nome do usuário (opcional inicialmente)

    private Boolean googleAuth; // Indica se o usuário se cadastrou via Google

    // Adicione outros campos relevantes conforme o projeto evoluir
}