package com.edulibre.api.usuario.service;

import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.usuario.dto.ActualizarPerfilRequest;
import com.edulibre.api.usuario.dto.UsuarioResponse;
import com.edulibre.api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public UsuarioResponse actual() {
        return toResponse(currentUserService.requireCurrentUser());
    }

    @Transactional
    public UsuarioResponse actualizar(ActualizarPerfilRequest request) {
        Usuario usuario = currentUserService.requireCurrentUser();
        if (request.nombre() != null) {
            usuario.setNombre(request.nombre().trim());
        }
        return toResponse(usuario);
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getCreadoEn());
    }
}
