package com.sales.application.auth.usecase;

import com.sales.domain.auth.entity.User;
import com.sales.domain.auth.port.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetAuthenticatedUserUseCase {

    @Inject
    UserRepository userRepository;

    public User execute(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado com id: " + userId
                ));
    }
}
