package com.edulibre.api.auth.controller;

import com.edulibre.api.auth.dto.AuthResponse;
import com.edulibre.api.auth.dto.LoginRequest;
import com.edulibre.api.auth.dto.RegisterRequest;
import com.edulibre.api.auth.service.AuthService;
import com.edulibre.api.security.jwt.AuthCookieService;
import com.edulibre.api.usuario.dto.UsuarioResponse;
import com.edulibre.api.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final AuthCookieService authCookieService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthService.AuthResultado resultado = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, authCookieService.crearCookie(resultado.token()).toString())
                .body(resultado.response());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.AuthResultado resultado = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieService.crearCookie(resultado.token()).toString())
                .body(resultado.response());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.limpiarCookie().toString())
                .build();
    }

    @GetMapping("/me")
    public UsuarioResponse me() {
        return usuarioService.actual();
    }
}
