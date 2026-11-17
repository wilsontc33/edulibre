package com.edulibre.api.auth.service;

import com.edulibre.api.auth.dto.AuthResponse;
import com.edulibre.api.auth.dto.LoginRequest;
import com.edulibre.api.auth.dto.RegisterRequest;
import com.edulibre.api.security.jwt.JwtService;
import com.edulibre.api.shared.exception.ConflictException;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.shared.util.Rol;
import com.edulibre.api.usuario.entity.Usuario;
import com.edulibre.api.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.ESTUDIANTE)
                .build();

        usuarioRepository.save(usuario);
        return buildAuthResponse(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return buildAuthResponse(usuario);
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        UserDetails userDetails = User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .build();

        String token = jwtService.generateToken(userDetails, usuario.getRol().name(), usuario.getNombre());
        return new AuthResponse(token, usuario.getEmail(), usuario.getNombre(), usuario.getRol().name());
    }
}
