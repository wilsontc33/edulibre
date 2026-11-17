package com.edulibre.api.usuario.controller;

import com.edulibre.api.usuario.dto.ActualizarPerfilRequest;
import com.edulibre.api.usuario.dto.UsuarioResponse;
import com.edulibre.api.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public UsuarioResponse me() {
        return usuarioService.actual();
    }

    @PutMapping("/me")
    public UsuarioResponse actualizar(@Valid @RequestBody ActualizarPerfilRequest request) {
        return usuarioService.actualizar(request);
    }
}
