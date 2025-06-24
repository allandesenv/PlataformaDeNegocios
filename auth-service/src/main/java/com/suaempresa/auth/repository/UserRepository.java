package com.suaempresa.auth.repository;

import com.suaempresa.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Indica que esta interface é um componente de repositório
public interface UserRepository extends JpaRepository<User, Long> {

    // Método para encontrar um usuário pelo e-mail
    Optional<User> findByEmail(String email);

    // Você pode adicionar outros métodos de busca aqui, se necessário
}