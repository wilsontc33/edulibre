package com.edulibre.api.security.service;

import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.usuario.entity.Usuario;
import com.edulibre.api.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(authentication.getName());
    }

    public Usuario requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }
}
